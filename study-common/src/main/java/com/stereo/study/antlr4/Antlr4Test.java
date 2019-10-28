package com.stereo.study.antlr4;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuj-ai on 2019/9/17.
 */
public class Antlr4Test {

    public static void main(String[] args) throws IOException {
        ANTLRInputStream inputStream = new ANTLRInputStream(" a = 1 + 2 + 3 * 4+ 6 / 2 \n");
        ExprLexer lexer = new ExprLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokenStream);
        ParseTree parseTree = parser.prog();
        EvalVisitor visitor = new EvalVisitor();
        Integer rtn = visitor.visit(parseTree);
        System.out.println("#result# = " + rtn.toString());
        System.out.println(parseTree.toStringTree(parser));

//        String SQL = "Windows2000";
//        String Reg = "Windows(?=95|98|NT|2000)";
//
//        Pattern pattern = Pattern.compile(Reg, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(SQL);
//        while (matcher.find()) {
//            System.out.println("匹配：" + matcher.group());
//        }
    }
}
