package uedson.reis.models;

public class Result {

	private Object data;
	private String message;
	private Boolean success = true;
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
	@Override
	public String toString() {
		return "ReturnType [data=" + data + ", success=" + success + ", message=" + message + "]";
	}
}
