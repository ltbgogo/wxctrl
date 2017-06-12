package com.abc.wxctrl.utility.spec;

public interface InOutProcessor<IN, OUT> {

	OUT process(IN in) throws Exception;
}
