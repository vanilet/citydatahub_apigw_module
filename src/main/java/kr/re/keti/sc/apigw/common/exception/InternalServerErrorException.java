package kr.re.keti.sc.apigw.common.exception;

import kr.re.keti.sc.apigw.common.ResponseCode;

@ResponseCodeType(ResponseCode.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends BaseException {

	private static final long serialVersionUID = -1598584115757128682L;

	public InternalServerErrorException(String detailDescription) {
		super(detailDescription);
	}
}