package kr.re.keti.sc.apigw.common.exception;

import kr.re.keti.sc.apigw.common.ResponseCode;

@ResponseCodeType(ResponseCode.TOO_MANY_REQUETS)
public class TooManyRequestException extends BaseException {

    private static final long serialVersionUID = -3268790656029162264L;

    public TooManyRequestException(String detailDescription) {
        super(detailDescription);
    }
}
