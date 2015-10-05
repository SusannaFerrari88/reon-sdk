package io.reon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import io.reon.http.HttpException;
import io.reon.http.Method;
import io.reon.http.Request;
import io.reon.http.Response;

public class AppInfoEndpoint extends AppEndpoint {
	public static final String SERVICES_JSON = "/services.json";

	private JSONObject servicesJson = null;

	public AppInfoEndpoint(WebAppContext ctx) {
		super(ctx);
	}

	protected Method httpMethod() {
		return Method.GET;
	}

	protected String originalPath() {
		return SERVICES_JSON;
	}

	private Pattern pattern = Pattern.compile(originalPath());

	@Override
	protected Pattern getPattern() {
		return pattern;
	}

	private String pkgName() {
		return getContext().getPackageName();
	}

	private String appName() {
		CharSequence charSequence = getContext().getApplicationInfo().loadLabel(getContext().getPackageManager());
		return charSequence.toString();
	}

	private JSONObject createServicesJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", pkgName());
		json.put("name", appName());
		JSONArray resources = new JSONArray();
		for (Info key : getWebContext().getEndpointInfos()) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("method", key.getMethod().toString());
			jsonRes.put("url", key.getUri());
			resources.put(jsonRes);
		}
		json.put("resources",resources);
		return json;
	}

	public JSONObject getServicesJson() {
		if (servicesJson == null) try {
			servicesJson = createServicesJson();
		} catch (JSONException e) {
			e.printStackTrace();
			servicesJson = new JSONObject();
		}
		return servicesJson;
	}

	@Override
	public Response invoke(String uri, Request request) throws HttpException, IOException {
		return Response.ok().withId(request.getId()).withBody(getServicesJson());
	}
}
