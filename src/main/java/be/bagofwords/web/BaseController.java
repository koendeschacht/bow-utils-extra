package be.bagofwords.web;

import be.bagofwords.logging.Log;
import be.bagofwords.minidepi.annotations.Property;
import org.apache.commons.lang3.exception.ExceptionUtils;
import spark.Request;
import spark.Response;
import spark.RouteImpl;

public abstract class BaseController extends RouteImpl {

    private String path;
    private String method;
    private boolean allowCORS;

    @Property(value = "web.host", orFrom = "bow-utils-extra.properties")
    private String webHost;

    public BaseController(String path) {
        this(path, "GET");
    }

    public BaseController(String path, String method) {
        this(path, method, false, "*/*");
    }

    public BaseController(String path, String method, boolean allowCORS, String acceptType) {
        super(path, acceptType);
        this.path = path;
        this.method = method;
        this.allowCORS = allowCORS;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public boolean isAllowCORS() {
        return allowCORS;
    }

    @Override
    public Object handle(Request request, Response response) {
        try {
            if (isAllowCORS()) {
                response.header("Access-Control-Allow-Origin", webHost);
                response.header("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD");
                response.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                response.header("Access-Control-Allow-Credentials", "true");
                if ("OPTIONS".equals(request.requestMethod())) {
                    return "";
                }
            }
            return handleRequest(request, response);
        } catch (Exception exp) {
            response.status(500);
            Log.e("Received exception while rendering " + this.getClass() + " for url " + getPath(), exp);
            String stackTrace = ExceptionUtils.getStackTrace(exp);
            return "<pre>" + stackTrace + "</pre>";
        }
    }

    protected abstract Object handleRequest(Request request, Response response) throws Exception;

}
