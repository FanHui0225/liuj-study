package com.stereo.study.rpc.io.factory;

import com.stereo.study.rpc.io.Input;
import com.stereo.study.rpc.utils.FreeList;
import com.stereo.study.rpc.io.GeneralInput;
import com.stereo.study.rpc.io.GeneralOutput;
import com.stereo.study.rpc.io.Output;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * IO工厂
 * 
 * @author liujing
 */
public class IOFactory {

	public static final Logger log = Logger.getLogger(IOFactory.class
			.getName());

	private SerializerFactory _serializerFactory;
	private SerializerFactory _defaultSerializerFactory;

	private final FreeList<Input> _freeRPCInput = new FreeList<Input>(32);
	private final FreeList<Output> _freeRPCOutput = new FreeList<Output>(32);

	private final FreeList<GeneralInput> _freeRPCGenInput = new FreeList<GeneralInput>(
			32);
	private final FreeList<GeneralOutput> _freeRPCGenOutput = new FreeList<GeneralOutput>(
			32);

	public IOFactory() {
		_defaultSerializerFactory = SerializerFactory.createDefault();
		_serializerFactory = _defaultSerializerFactory;
	}

	public void setSerializerFactory(SerializerFactory factory) {
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory() {
		if (_serializerFactory == _defaultSerializerFactory) {
			_serializerFactory = new SerializerFactory();
		}
		return _serializerFactory;
	}

	public Input createRPCInput(InputStream is) {
		Input in = createRPCInput();
		in.init(is);
		return in;
	}

	public Input createRPCInput() {
		Input in = _freeRPCInput.allocate();
		if (in == null) {
			in = new Input();
			in.setSerializerFactory(getSerializerFactory());
		}
		return in;
	}

	public Output createRPCOutput(OutputStream os) {
		Output out = createRPCOutput();
		out.init(os);
		return out;
	}

	public Output createRPCOutput() {
		Output out = _freeRPCOutput.allocate();
		if (out == null) {
			out = new Output();
			out.setSerializerFactory(getSerializerFactory());
		}
		return out;
	}

	public void freeRPCInput(Input in) {
		if (in == null)
			return;
		in.free();
		_freeRPCInput.free(in);
	}

	public void freeRPCOutput(Output out) {
		if (out == null)
			return;
		out.free();
		_freeRPCOutput.free(out);
	}
}