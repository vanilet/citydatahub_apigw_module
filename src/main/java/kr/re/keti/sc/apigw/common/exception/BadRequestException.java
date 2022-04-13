package kr.re.keti.sc.apigw.common.exception;

import kr.re.keti.sc.apigw.common.ResponseCode;

@ResponseCodeType(ResponseCode.BAD_REQUEST)
public class BadRequestException extends BaseException {

	private static final long serialVersionUID = 325647261102179280L;

	public BadRequestException(String detailDescription) {
		super(detailDescription);
	}
}