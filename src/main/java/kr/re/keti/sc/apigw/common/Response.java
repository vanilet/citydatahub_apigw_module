package kr.re.keti.sc.apigw.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class Response {
	
	@JsonProperty
	private String messageId;

	@JsonProperty
	private ResponseCode type;

	private Object title;
	
	private Object detail;

	public Response () {
	}
	
	public Response (ResponseCode type, Object title ,Object detail) {
		this.type = type;
		this.title = title;
		this.detail = detail;
	}
	
	public Response (ResponseCode type, Object detail) {
		this.type = type;
		this.title = type.getDetailDescription();
		this.detail = detail;
	}

	public Response (String messageId) {
		this.messageId = messageId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public ResponseCode getType() {
		return type;
	}

	public void setType(ResponseCode type) {
		this.type = type;
	}

	public Object getTitle() {
		return title;
	}

	public void setTitle(Object title) {
		this.title = title;
	}

	public Object getDetail() {
		return detail;
	}

	public void setDetail(Object detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "Response [messageId=" + messageId + ", type=" + type + ", title=" + title + ",  detail=" + detail + "]";
	}
}
