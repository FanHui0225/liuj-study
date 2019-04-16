package com.stereo.study.rpc.api;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public interface IService {

	public void init(ServletConfig config) throws ServletException;

	public void destroy();
}
