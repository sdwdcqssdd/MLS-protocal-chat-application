package assistant.backend.entity;

/**
 * data class to standardize HTTP response
 */
public class Response {
    private Boolean ok;

    private String message;

    private Object data;

    static public Response makeResponse(Boolean ok, String message, Object data) {
        Response response = new Response();
        response.setOk(ok);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    static public Response makeResponse(Boolean ok, String message) {
        return makeResponse(ok, message, null);
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
