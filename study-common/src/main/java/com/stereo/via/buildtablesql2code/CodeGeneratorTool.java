package com.stereo.via.buildtablesql2code;


import com.stereo.via.buildtablesql2code.model.ClassInfo;
import com.stereo.via.buildtablesql2code.util.TableParseUtil;

import java.io.IOException;

public class CodeGeneratorTool {

    /**
     * process Table Into ClassInfo
     *
     * @param tableSql
     * @return
     */
    public static ClassInfo processTableIntoClassInfo(String tableSql) throws IOException {
        return TableParseUtil.processTableIntoClassInfo(tableSql);
    }

}