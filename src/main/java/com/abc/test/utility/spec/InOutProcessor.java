package com.abc.test.utility.spec;

public interface InOutProcessor<IN, OUT> {

	OUT process(IN in) throws Exception;
}
