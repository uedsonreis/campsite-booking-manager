package uedson.reis.models;

public class Result {

	private Object data;
	private String message;
	
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

	@Override
	public String toString() {
		return "ReturnType [data=" + data + ", message=" + message + "]";
	}

}