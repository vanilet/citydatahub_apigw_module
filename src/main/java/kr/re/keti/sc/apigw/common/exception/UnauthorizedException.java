package kr.re.keti.sc.apigw.common.exception;

import kr.re.keti.sc.apigw.common.ResponseCode;

@ResponseCodeType(ResponseCode.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {

	private static final long serialVersionUID = 3800354356842792724L;

	public UnauthorizedException(String detailDescription) {
		super(detailDescription);
	}
}