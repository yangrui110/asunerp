package com.hanlin.fadp.utils;

import com.hanlin.fadp.base.util.ScriptUtil;
import com.hanlin.fadp.base.util.UtilIO;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * word操作工具类
 *
 * @author 陈林
 */
public class UtilWord {
    public static final String styleGap = "\t\t";// 解析中间结果的样式分割符

    // 英文版的word样式转换为中文样式
    public static String currectStyleName(String name) {
        if (UtilValidate.isEmpty(name)) {
            return "正文" + styleGap;
        }
        name = name.toLowerCase();
        String newName = name + styleGap;
        if (newName.startsWith("normal")) {
            newName = newName.replace("normal", "正文");
        } else if (newName.startsWith("heading")) {
            newName = newName.replace("heading", "标题");
        } else if (newName.startsWith("default paragraph font")) {
            newName = newName.replace("default paragraph font", "默认段落字体");
        } else if (newName.startsWith("plain text")) {
            newName = newName.replace("plain text", "纯文本");
        }
        return newName;
    }

    /**
     * 垂直合并单元格
     *
     * @param table
     * @param col
     * @param fromRow
     * @param toRow
     */
    public static void mergeCellVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            CTVMerge vmerge = CTVMerge.Factory.newInstance();
            if (rowIndex == fromRow) {
                // The first merged cell is set with RESTART merge value
                vmerge.setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                vmerge.setVal(STMerge.CONTINUE);
            }
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            // Try getting the TcPr. Not simply setting an new one every time.
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                tcPr.setVMerge(vmerge);
            } else {
                // only set an new TcPr if there is not one already
                tcPr = CTTcPr.Factory.newInstance();
                tcPr.setVMerge(vmerge);
                cell.getCTTc().setTcPr(tcPr);
            }
        }
    }

    /**
     * 水平合并单元格
     *
     * @param table
     * @param row
     * @param fromCol
     * @param toCol
     */
    public static void mergeCellHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
            CTHMerge hmerge = CTHMerge.Factory.newInstance();
            if (colIndex == fromCol) {
                // The first merged cell is set with RESTART merge value
                hmerge.setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                hmerge.setVal(STMerge.CONTINUE);
            }
            XWPFTableCell cell = table.getRow(row).getCell(colIndex);
            // Try getting the TcPr. Not simply setting an new one every time.
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                tcPr.setHMerge(hmerge);
            } else {
                // only set an new TcPr if there is not one already
                tcPr = CTTcPr.Factory.newInstance();
                tcPr.setHMerge(hmerge);
                cell.getCTTc().setTcPr(tcPr);
            }
        }
    }

    /**
     * 拷贝一个table但是没有加入doc
     *
     * @param sourceTable,
     * @return
     */
    public static XWPFTable copyTable(XWPFDocument doc, XWPFTable sourceTable) {
        // Copying a existing table
        CTTbl ctTbl = CTTbl.Factory.newInstance(); // Create a new CTTbl for the
        // new table
        ctTbl.set(sourceTable.getCTTbl()); // Copy the template table's CTTbl
        XWPFTable table2 = new XWPFTable(ctTbl, doc); // Create a new table
        // using the CTTbl upon
        return table2;
    }

    /**
     * 拷贝一行
     *
     * @param doc
     * @param row
     * @param targetTable
     * @return
     */
    public static XWPFTableRow copyTableRow(XWPFDocument doc, XWPFTableRow row, XWPFTable targetTable) {
        CTRow ctRow = CTRow.Factory.newInstance();
        ctRow.set(row.getCtRow());
        XWPFTableRow row2 = new XWPFTableRow(ctRow, targetTable);
        return row2;
    }

    public static void cloneParagraph(XWPFParagraph clone, XWPFParagraph source) {
//		CTPPr pPr = clone.getCTP().isSetPPr() ? clone.getCTP().getPPr() : clone.getCTP().addNewPPr();
//	    pPr.set(source.getCTP().getPPr());

        clone.getCTP().setPPr(source.getCTP().getPPr());
        cloneNumbering(clone, source);
//		clone.setStyle(source.getStyle());
//		clone.setStyle("Heading1");
        for (XWPFRun r : source.getRuns()) {
            XWPFRun nr = clone.createRun();
            cloneRun(nr, r);
        }
    }

    public static void cloneNumbering(XWPFParagraph clone, XWPFParagraph source) {
        XWPFNumbering numbering = source.getDocument().getNumbering();
        int currentIlvl = -1;
        CTNumPr numPr = null;
        // word使用列表的两种方式分别是：
        // 1. 先指定文本的样式（例如先选择文本为一级标题）再指定文本应用的列表样式
        // 2. 直接选择使用标题列表
        // 以下判断就是针对上述两种情况作出的处理
        if ((source.getCTP().getPPr() != null) && (source.getCTP().getPPr().getNumPr() != null)) {
            numPr = source.getCTP().getPPr().getNumPr();
        } else {
            XWPFStyle style = source.getDocument().getStyles().getStyle(source.getStyleID());
            if (style != null && style.getCTStyle() != null && style.getCTStyle().getPPr() != null) {
                numPr = style.getCTStyle().getPPr().getNumPr();
            }
        }
        BigInteger currentNumID = null;
        if (numPr != null) {
            if (numPr.getIlvl() != null) {
                currentIlvl = numPr.getIlvl().getVal().intValue();
            } else {
                currentIlvl = 0;
            }
            if (numPr.getNumId() != null) {
                currentNumID = numPr.getNumId().getVal();
            }
        }

        if ((currentNumID != null) && (numbering != null) && currentIlvl != -1) {
            clone.getCTP().getPPr().setNumPr(numPr);
        }

    }


    public static void cloneRun(XWPFRun clone, XWPFRun source) {
        clone.getCTR().setBrArray(source.getCTR().getBrArray());
        CTRPr rPr = clone.getCTR().isSetRPr() ? clone.getCTR().getRPr() : clone.getCTR().addNewRPr();
        rPr.set(source.getCTR().getRPr());
        String text = source.getText(0);
        if (UtilValidate.isNotEmpty(text)) {
            clone.setText(text);
        }
    }

    @Test
    private static void testMergeCell() throws Exception {

        @SuppressWarnings("resource")
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("The table:");

        // create table
        XWPFTable table = document.createTable(3, 5);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                table.getRow(row).getCell(col).setText("row " + row + ", col " + col);
            }
        }

        // create and set column widths for all columns in all rows
        // most examples don't set the type of the CTTblWidth but this
        // is necessary for working in all office versions
        for (int col = 0; col < 5; col++) {
            CTTblWidth tblWidth = CTTblWidth.Factory.newInstance();
            tblWidth.setW(BigInteger.valueOf(1000));
            tblWidth.setType(STTblWidth.DXA);
            for (int row = 0; row < 3; row++) {
                CTTcPr tcPr = table.getRow(row).getCell(col).getCTTc().getTcPr();
                if (tcPr != null) {
                    tcPr.setTcW(tblWidth);
                } else {
                    tcPr = CTTcPr.Factory.newInstance();
                    tcPr.setTcW(tblWidth);
                    table.getRow(row).getCell(col).getCTTc().setTcPr(tcPr);
                }
            }
        }

        // using the merge methods
        mergeCellVertically(table, 0, 0, 1);
        mergeCellVertically(table, 0, 1, 2);
        mergeCellHorizontally(table, 1, 2, 3);
        mergeCellHorizontally(table, 2, 1, 4);

        paragraph = document.createParagraph();

        FileOutputStream out = new FileOutputStream("create_table.docx");
        document.write(out);

        System.out.println("create_table.docx written successully");
    }

    public static void main(String[] args) throws Exception {
        testExportReport();
    }

    /***
     * 测试根据模板导出报表
     *
     * @throws Exception
     */
    @Test
    protected static void testExportReport() throws Exception {
       System.setProperty("fadp.home", System.getProperty("user.dir"));
        String testDir = System.getProperty("fadp.home") + "/hot-deploy/fadp/template/";
        File templateFile=new File(testDir + "relationExport.docx");



        Map<String, Object> context = FastMap.newInstance();
        Map<String, Object> relation = FastMap.newInstance();
        context.put("list", UtilMisc.toList(relation));


        relation.put("upstreamDocName", "需求文档");
        relation.put("docName", "设计文档");
        relation.put("upstreamDocTitle", "需求文档");
        relation.put("docTitle", "设计文档");
        relation.put("upReqTitle", "需求");
        relation.put("upSectionTitle", "章节");
        relation.put("reqTitle", "标识");
        relation.put("sectionTitle", "章节");

        Map<String, Object> req = FastMap.newInstance();
        relation.put("reqList", UtilMisc.toList(req, req));

        req.put("reqName", "设计1");
        req.put("section", "标题1");
        req.put("upReqName", "需求1");
        req.put("upSection", "标题1");

        WordxPaser wordxPaser = new WordxPaser(templateFile, context);
        XWPFDocument doc = wordxPaser.parsedDoc;
        FileOutputStream out = new FileOutputStream(testDir + "result2.docx");
        doc.write(out);

        out.flush();
        out.close();
    }

    public static XWPFDocument exportDocx(Map<String, Object> context) throws FileNotFoundException, IOException {
        String testDir = System.getProperty("user.dir") + "/hot-deploy/retest/template/";
        return new WordxPaser(new FileInputStream(testDir + "relationExport.docx"), context).parsedDoc;
    }

    public static XWPFDocument exportDocx(String templateDocPath, Map<String, Object> context) throws FileNotFoundException, IOException {
        return new WordxPaser(new FileInputStream(templateDocPath), context).parsedDoc;
    }

    /**
     * wordx模板解析器，支持forEach循环，合并单元格，变量表达式
     *
     * @author 陈林
     */
    public static class WordxPaser {
        static String ifPrefix = "$if_";
        int uniqueNum = 1;
        Pattern forEachBeginReg;// forEach开始正则
        Pattern forEachEndReg;// forEach结束正则
        Pattern variableReg;// 变量表达式正则
        Pattern mergeReg;// 合并单元格正则

        Pattern ifBeginReg;//if开始
        Pattern ifEndReg;//if结束

        Map<String, Object> rootContext;// 顶级上下文
        Stack<String> stack = new Stack<>();// 存储forEach
        Scope currentScope;// 当前作用域

        public XWPFDocument parsedDoc = new XWPFDocument();//编译完的word文档对象

        public WordxPaser(File templateFile, Map<String, Object> context) throws IOException {
            String tempFilePath = System.getProperty("fadp.home") + "/runtime/tmp/" + System.currentTimeMillis() + (Math.random() * 10000);

            File tempFile = new File(tempFilePath);
            UtilIO.copy(new FileInputStream(templateFile), true, new FileOutputStream(tempFile), true);

            parsedDoc = new XWPFDocument(new FileInputStream(tempFile));

            List<IBodyElement> bodyElements = parsedDoc.getBodyElements();
            for (int i = bodyElements.size(); i >= 0; i--) {
                parsedDoc.removeBodyElement(i);
            }

            defaultParse(new FileInputStream(templateFile), context);

        }

        @Deprecated
        public WordxPaser(InputStream in, File outFile, Map<String, Object> context) throws IOException {
            parsedDoc = new XWPFDocument(new FileInputStream(outFile));
            defaultParse(in, context);
        }

        public WordxPaser(InputStream in, Map<String, Object> context) throws IOException {
            defaultParse(in, context);
        }

        public void defaultParse(InputStream in, Map<String, Object> context) throws IOException {
            startParse(in, context
                    , "\\{\\$foreach\\(([^\\)\\}]*)\\)\\}"
                    , "\\{\\$endfor\\}"
                    , "\\{\\$([^\\}]*)\\}"
                    , "\\{\\$Merge\\(([^\\)\\}]*)\\)\\}"
                    , "\\{\\$if\\(([^\\)\\}]*)\\)\\}"
                    , "\\{\\$endif\\}"
            );
        }

        public WordxPaser(InputStream in, Map<String, Object> context, String forEachBeginReg, String forEachEndReg,
                          String variableReg, String mergeReg, String ifBeginReg, String ifEndReg) throws IOException {
            startParse(in, context, forEachBeginReg, variableReg, variableReg, mergeReg, ifBeginReg, ifEndReg);
        }

        public void startParse(InputStream in, Map<String, Object> context, String forEachBeginReg, String forEachEndReg,
                               String variableReg, String mergeReg, String ifBeginReg, String ifEndReg) throws IOException {


            XWPFDocument doc = new XWPFDocument(in);
            this.rootContext = context;
            this.forEachBeginReg = Pattern.compile(forEachBeginReg);
            this.forEachEndReg = Pattern.compile(forEachEndReg);
            this.variableReg = Pattern.compile(variableReg);
            this.mergeReg = Pattern.compile(mergeReg);
            this.ifBeginReg = Pattern.compile(ifBeginReg);
            this.ifEndReg = Pattern.compile(ifEndReg);
            List<IBodyElement> bodyElements = doc.getBodyElements();
            int size = bodyElements.size();
            currentScope = new Scope(Scope.root, null);
            stack.push(currentScope.name);
            //编译模版，构造作用域
            for (int i = 0; i < size; i++) {
                IBodyElement ele = bodyElements.get(i);
                boolean shouldSave = true;
                if (ele instanceof XWPFParagraph) {
                    //如果当前段落是语法标记（例如forEach），则做两件事情：
                    //1. 构建相应的作用域。2. 标记当前段落不参与文档输出
                    //如果当前段落不是是语法标记，则应该保留
                    XWPFParagraph p = (XWPFParagraph) ele;
                    String text = p.getText();
                    //这里考虑到一个段落中可以有多个语法标记，所以将每个match都执行一遍
                    boolean shouldNotSave = matchBegin(text, this.forEachBeginReg);
                    shouldNotSave = shouldNotSave || matchBegin(text, this.ifBeginReg);
                    shouldNotSave = shouldNotSave || matchEnd(text, this.forEachEndReg);
                    shouldNotSave = shouldNotSave || matchEnd(text, this.ifEndReg);
                    shouldSave = !shouldNotSave;
                }//不是段落的其它元素要保留，例如表格。
                if (shouldSave && currentScope != null) {
                    currentScope.addChild(ele);
                }
            }
            //开始从根作用域开始输出文档
            loopScope(Scope.allScopeMap.get(Scope.root), context);

        }

        //匹配开始标记
        private boolean matchBegin(String text, Pattern reg) {
            Matcher matcher = reg.matcher(text);
            if (matcher.find()) {
                String name = matcher.group(1);
                String condition = null;
                if (reg == ifBeginReg) {
                    //给if也创造一个作用域，但是它的名称只是一个标识，不代表上下文的名称,我们将在实际解析时处理这个情况
                    condition = name;
                    name = ifPrefix + getUniqueNum();

                }
                beginNewScope(name, condition);
                return true;
            }
            return false;
        }

        /**
         * 开始新的作用域
         *
         * @param name
         * @param condition
         */
        private void beginNewScope(String name, String condition) {

            Object[] array = stack.toArray();
            String key = "";
            for (Object obj : array) {
                key = key + obj + ".";
            }
            stack.push(name);
            currentScope = new Scope(key + name, condition);
        }

        //匹配结束标记
        private boolean matchEnd(String text, Pattern reg) {
            Matcher matcher = reg.matcher(text);
            if (matcher.find()) {
                endScope();
                return true;
            }
            return false;
        }

        private void endScope() {
            stack.pop();
            if (stack.isEmpty()) {

                currentScope = null;
            } else {
                currentScope = Scope.allScopeMap
                        .get(currentScope.name.substring(0, currentScope.name.lastIndexOf('.')));
            }
        }

        @SuppressWarnings({"unchecked"})
        private void loopScope(Scope scope, Map<String, Object> context) {
            for (Object child : scope.children) {
                if (child instanceof Scope) {
                    Scope childScope = (Scope) child;
                    if (childScope.condition != null) {//在作用域内

                        try {
                            boolean ok = (boolean) ScriptUtil.evaluate("groovy", childScope.condition, null, context);
                            if (ok) {//if作用域沿用其父作用域
                                loopScope(childScope, context);
                            }
                            continue;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    List<Map<String, Object>> childContextList = (List<Map<String, Object>>) context
                            .get(childScope.shortName);
                    if (childContextList == null) {
                        childContextList = null;
                    }
                    if (isLoopRow(childScope)&&UtilValidate.isNotEmpty(childContextList)) {

                        loopScope(childScope, childContextList.get(0));
                        for (int i = 1; i < childContextList.size(); i++) {
                            loopFillRow(childScope, childContextList.get(i));
                        }
                    } else {
                        for (Map<String, Object> childContext : childContextList) {
                            loopScope(childScope, childContext);
                        }
                    }

                } else {
                    if (child instanceof XWPFParagraph) {
                        // 拷贝一段
                        XWPFParagraph newp = parsedDoc.createParagraph();
                        cloneParagraph(newp, (XWPFParagraph) child);
                        fillParagraph(newp, context);

                    } else if (child instanceof XWPFTable) {
                        //填充一个表格（或者是一个表头）
                        //说明：当向word中先后插入两个表格并紧邻，那么这两个表格就会合并为一个表格，
                        //所以在这里先插入一个表头，再后续插入数据行
                        XWPFTable newTable = UtilWord.copyTable(parsedDoc, (XWPFTable) child);
                        fillTable(newTable, context);
                        parsedDoc.setTable(getTablePos(parsedDoc, parsedDoc.createTable()), newTable);
//							int tablePos = getTablePos(parsedDoc, parsedDoc.createTable());
//							parsedDoc.setTable(tablePos, (XWPFTable) child);
//							XWPFTable newTable =parsedDoc.getTables().get(tablePos);
//							fillTable(newTable, context);
                    } else {
                        System.err.println("暂未解析" + child);
                    }
                }

            }


        }

        /**
         * 是否在构造表格
         *
         * @param scope
         * @return
         */
        protected boolean isLoopRow(Scope scope) {
            return scope.children.size() == 1 && scope.children.get(0) instanceof XWPFTable;
        }

        private void fillTable(XWPFTable table, Map<String, Object> context) {
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                List<Map<String, Integer>> mergeList = fillRow(table, row, context);
//				for (Map<String, Integer> map : mergeList) {
//					mergeCellVertically(table, map.get("cellIndex"),map.get("fromRow"),map.get("toRow"));
//				}

            }

        }

        /**
         * 填充表格数据
         *
         * @param scope
         * @param context
         */
        private void loopFillRow(Scope scope, Map<String, Object> context) {
            XWPFTable table = (XWPFTable) scope.eleList.get(0);
            XWPFTableRow row = table.getRow(0);
            int tableIndex = parsedDoc.getTables().size() - 1;

            XWPFTable newTable = parsedDoc.getTables().get(tableIndex);
            XWPFTableRow newRow = UtilWord.copyTableRow(parsedDoc, row, newTable);
            // XWPFTableRow newRow = UtilWord.copyTableRow(doc, row,
            // tableIndex);
            // int rowNum = newTable.getNumberOfRows();
            // XWPFTableRow newRow = newTable.getRow(rowNum - 1);


            List<Map<String, Integer>> mergeList = fillRow(newTable, newRow, context);

            newTable.addRow(newRow);

            newTable = copyTable(parsedDoc, newTable);
            for (Map<String, Integer> map : mergeList) {
                mergeCellVertically(newTable, map.get("cellIndex"), map.get("fromRow"), map.get("toRow"));
            }

            parsedDoc.setTable(tableIndex, newTable);


        }

        private List<Map<String, Integer>> fillRow(XWPFTable table, XWPFTableRow row, Map<String, Object> context) {
            List<Map<String, Integer>> returnValue = FastList.newInstance();
            List<XWPFTableCell> tableCells = row.getTableCells();
            // XWPFTable table = row.getTable();
            int rowNum = table.getNumberOfRows();
            for (int cellIndex = 0; cellIndex < tableCells.size(); cellIndex++) {
                XWPFTableCell cell = tableCells.get(cellIndex);
                String text = cell.getText();
                Matcher matcher = mergeReg.matcher(text);
                if (matcher.find()) {// 需要合并行
                    fillMergeCell(cell, context);
                    // changeParagraphText(cell.getParagraphs().get(0),
                    // replaceText(text, mergeReg, context));
                    int fromRow = -1;
                    for (int rowIndex = rowNum - 1; rowIndex > -1; rowIndex--) {
                        // 向上行查看，单元格内容相同就合并。
                        XWPFTableRow prevRow = table.getRow(rowIndex);
                        XWPFTableCell prevCell = prevRow.getCell(cellIndex);
                        String prevText = prevCell.getText();
                        String cellText = cell.getText();
                        if (UtilValidate.areEqual(cellText, prevText)) {
                            // 与上一行的对应列值相同
                            fromRow = rowIndex;
                        } else {
                            // 与上一行内容不相同，跳出循环
                            break;
                        }
                    }
                    if (fromRow != -1) {
                        // 合并
                        Map<String, Integer> map = FastMap.newInstance();
                        returnValue.add(map);
                        map.put("cellIndex", cellIndex);
                        map.put("fromRow", fromRow);
                        map.put("toRow", rowNum);
                    }
                } else {
                    fillCell(cell, context);
                }

            }
            return returnValue;

        }

        private void fillCell(XWPFTableCell cell, Map<String, Object> context) {
            List<XWPFParagraph> paragraphs = cell.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                fillParagraph(paragraph, context);
            }
        }

        private void fillMergeCell(XWPFTableCell cell, Map<String, Object> context) {
            List<XWPFParagraph> paragraphs = cell.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                changeParagraphText(paragraph, replaceText(paragraph.getParagraphText(), mergeReg, context));
            }
        }

        private void fillParagraph(XWPFParagraph p, Map<String, Object> context) {
            changeParagraphText(p, replaceText(p.getParagraphText(), variableReg, context));
        }

        public void changeParagraphText(XWPFParagraph p, String newText) {
            if (p == null)
                return;
            List<XWPFRun> runs = p.getRuns();
            if (runs.size() > 0) {
                for (int i = runs.size() - 1; i > 0; i--) {
                    p.removeRun(i);
                }
                XWPFRun run = runs.get(0);
                run.setText(newText, 0);
            }

        }


        public int getTablePos(XWPFDocument doc, XWPFTable t) {
            return doc.getTablePos(doc.getPosOfTable(t));
        }

        private static String replaceText(String text, Pattern pattern, Map<String, Object> context) {
            Matcher matcher = pattern.matcher(text);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String complete = matcher.group(0);
                String variableName = matcher.group(1);
                Object variableValue = context.get(variableName);
                if (variableValue == null)
                    variableValue = "";
                int si = text.indexOf(complete);
                sb.append(text.substring(0, si));
                sb.append(variableValue);
                text = text.substring(si + complete.length());
            }
            sb.append(text);
            return sb.toString();
        }


        private int getUniqueNum() {
            return uniqueNum++;
        }

        /**
         * 作用域
         *
         * @author 陈林
         */
        public static class Scope {
            public static final String root = "root";
            String name;
            String shortName;
            String condition;
            // Map<String, Object> context;
            private List<IBodyElement> eleList = FastList.newInstance();
            private List<Object> children = FastList.newInstance();
            // 所有作用域
            public static Map<String, Scope> allScopeMap = FastMap.newInstance();
            public static List<IBodyElement> allElementList = FastList.newInstance();

            public Scope(String name, String condition) {
                this.condition = condition;
                if (!UtilValidate.areEqual(root, name)) {
                    String parentName;
                    if (name.contains(".")) {
                        int indexOfSep = name.lastIndexOf('.');
                        parentName = name.substring(0, indexOfSep);
                        shortName = name.substring(indexOfSep + 1);
                    } else {
                        parentName = root;
                        shortName = name;
                    }
                    Scope parentScope = allScopeMap.get(parentName);
                    parentScope.children.add(this);
                } else {
                    shortName = name;
                }

                this.name = name;

                allScopeMap.put(this.name, this);
            }

            public void addChild(IBodyElement ele) {
                eleList.add(ele);
                allElementList.add(ele);
                children.add(ele);
            }

            @Override
            public String toString() {
                return "{shortName:" + shortName + ",child:" + children + "}";
            }

        }
    }

}
