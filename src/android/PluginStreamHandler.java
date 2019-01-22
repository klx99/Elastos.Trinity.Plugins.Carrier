package org.elastos.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.net.ServerSocket;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.session.*;
import org.elastos.carrier.exceptions.ElastosException;
import org.json.JSONException;
import org.json.JSONObject;

public class PluginStreamHandler extends AbstractStreamHandler {
	private static String TAG = "PluginStreamHandler";

	public Stream mStream;
	public int mCode;
	public CallbackContext mCallbackContext = null;

	public PluginStreamHandler(CallbackContext callbackContext) {
		this.mCallbackContext = callbackContext;
	}

	public static PluginStreamHandler createInstance(Session session, int type, int options, CallbackContext callbackContext) throws ElastosException {
		PluginStreamHandler handler = new PluginStreamHandler(callbackContext);
		if (handler != null) {
			handler.mStream = session.addStream(StreamType.valueOf(type), options, handler);
			if (handler.mStream != null) {
				handler.mCode = System.identityHashCode(handler.mStream);
			}
		}
		return handler;
	}

	public JSONObject getAddressInfoJson(AddressInfo info) throws JSONException {
		JSONObject r = new JSONObject();
		r.put("type", info.getCandidateType().value());
		r.put("address", info.getAddress().getAddress().toString());
		r.put("port", info.getAddress().getPort());
		r.put("relatedAddress", info.getRelatedAddress().getAddress().toString());
		r.put("relatedPort", info.getRelatedAddress().getPort());
		return r;
	}

	public JSONObject getTransportInfoJson() throws JSONException, ElastosException{
		TransportInfo info = mStream.getTransportInfo();
		JSONObject r = new JSONObject();
		r.put("topology", info.getTopology().value());
		r.put("localAddr", getAddressInfoJson(info.getLocalAddressInfo()));
		r.put("remoteAddr", getAddressInfoJson(info.getRemoteAddressInfo()));
		return r;
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("objId", mCode);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

	@Override
	public void onStateChanged(Stream stream, StreamState state) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onStateChanged");
			r.put("state", state.value());
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStreamData(Stream stream, byte[] data) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onStreamData");
			r.put("data", Base64.encodeToString(data, Base64.DEFAULT));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onChannelOpen(Stream stream, int channel, String cookie) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelOpen");
			r.put("channel", channel);
			r.put("cookie", cookie);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void onChannelOpened(Stream stream, int channel) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelOpened");
			r.put("channel", channel);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChannelClose(Stream stream, int channel,  CloseReason reason) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelClose");
			r.put("channel", channel);
			r.put("reason", reason.value());
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onChannelData(Stream stream, int channel, byte[] data) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelData");
			r.put("channel", channel);
			r.put("data", Base64.encodeToString(data, Base64.DEFAULT));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void onChannelPending(Stream stream, int channel) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelPending");
			r.put("channel", channel);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChannelResume(Stream stream, int channel) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelResume");
			r.put("channel", channel);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}