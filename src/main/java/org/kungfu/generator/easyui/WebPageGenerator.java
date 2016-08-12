package org.kungfu.generator.easyui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.kungfu.generator.ColumnMeta;
import org.kungfu.generator.TableMeta;

import com.jfinal.kit.StrKit;

/**
 * Web Pages 生成器, 针对BJUI Web框架
 */
public class WebPageGenerator {
	// index.html template
	protected String indexPageBeginTemplate =
			"<#include \"/views/_crud.html\"/>%n%n" +
			"<@table title=\"%s\" url=\"/%s/page\">%n" +
			"\t<th data-options=\"field:'id', hidden: true\">%sID</th>%n";
	
	protected String tableContentTemplate =
			"\t<th data-options=\"field:'%s',width:50,align:'center'\">%s</th>%n";
			
	protected String indexPageMidTemplate =
			"</@table>%n%n" +
			"<@toolbar>%n" +
			"</@toolbar>%n%n" +
			"<@form saveOrUpdateUrl=\"/%s/saveOrUpdate\" destoryUrl=\"/%s/delete\">%n" +
			"\t<input type=\"hidden\" name=\"id\" />%n";
			
	protected String formContentTemplate =
			"\t<div class=\"fitem\">%n" +
			"\t\t<label>%s:</label>%n"  +
			"\t\t<input name=\"%s\" id=\"%s\" class=\"easyui-%s\" data-options=\"%s\">%n" +
			"\t</div>%n%n";
			
	protected String indexPageEndTemplate =
			"</@form>%n%n" +
			"<script type=\"text/javascript\">%n" +
			"\t$(function(){%n" +
			"\t\t// init combobox%n" +
			"\t\t//$('#fieldName').combobox('reload', '/your-data-url/or-json-file');%n%n" +
			"\t});%n" +
			"</script>%n";
	
	protected String webPageOutputDir;
	
	public WebPageGenerator(String webPageOutputDir) {
		if (StrKit.isBlank(webPageOutputDir))
			throw new IllegalArgumentException("webPageOutputDir can not be blank.");
		
		this.webPageOutputDir = webPageOutputDir;
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate Web Pages ...");
		for (TableMeta tableMeta : tableMetas)
			genIndexPageContent(tableMeta);
		wirtToFile(tableMetas, "index.html");
		
	}
	
	protected void genIndexPageContent(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genIndexBeginPage(tableMeta, ret);
		genTableContent(tableMeta, ret);
		genIndexMidPage(tableMeta, ret);
		genFormContent(tableMeta, ret);
		genIndexEndPage(ret);
		
		tableMeta.modelContent = ret.toString();
	}

	protected void genIndexBeginPage(TableMeta tableMeta, StringBuilder ret) {
		String s = tableMeta.modelName.toLowerCase().replaceAll("_", "");
		ret.append(String.format(indexPageBeginTemplate, tableMeta.remarks, s, tableMeta.remarks));
	}
	
	protected void genIndexMidPage(TableMeta tableMeta, StringBuilder ret) {
		String s = tableMeta.modelName.toLowerCase().replaceAll("_", "");
		ret.append(String.format(indexPageMidTemplate, s, s));
	}
	
	protected void genIndexEndPage(StringBuilder ret) {
		ret.append(String.format(indexPageEndTemplate));
	}
	
	private boolean noFilter(ColumnMeta columnMeta) {  
		if (columnMeta.isPrimaryKey.equalsIgnoreCase("PRI") || columnMeta.name.equals("Explains") || columnMeta.name.equals("Remarks") 
				|| columnMeta.name.equals("Logo_Photo_URL") || columnMeta.name.equals("User_Code") || columnMeta.name.equals("User_Name") 
				|| columnMeta.name.equals("Create_Time") || columnMeta.name.equals("Edit_Time")
				|| columnMeta.name.equals("userCode")  || columnMeta.name.equals("createTime") || columnMeta.name.equals("editTime"))
			return false;
		return true;
	}
	 
	protected void genTableContent(TableMeta tableMeta, StringBuilder ret) {
		for (ColumnMeta columnMeta : tableMeta.columnMetas) 
			if (noFilter(columnMeta)) {
				ret.append(String.format(tableContentTemplate, org.kungfu.util.StrKit.toCamelCase(columnMeta.name), columnMeta.remarks));
			}
	}

	protected void genFormContent(TableMeta tableMeta, StringBuilder ret) {
		
		for (ColumnMeta columnMeta : tableMeta.columnMetas) {
			if (columnMeta.name.equalsIgnoreCase("id") || columnMeta.name.equals("User_Code") || columnMeta.name.equals("User_Name")  || columnMeta.name.equals("Create_Time") || columnMeta.name.equals("Edit_Time")
					|| columnMeta.name.equals("userCode") || columnMeta.name.equals("user_id")   || columnMeta.name.equals("createTime") || columnMeta.name.equals("editTime") || columnMeta.name.equals("create_at") || columnMeta.name.equals("edit_at"))
				continue;
			
			boolean isTextArea = false;
			String controlType = "textbox";
			String dataOptions = ""; // required:true,validType:'number'
			if (columnMeta.type.contains("(") && columnMeta.type.contains(")") && org.kungfu.util.StrKit.hasDigit(columnMeta.type))
				isTextArea = Integer.parseInt(columnMeta.type.substring(columnMeta.type.indexOf('(')+1, columnMeta.type.indexOf(')'))) >= 128;
			if (isTextArea) {
				if (StrKit.isBlank(dataOptions)) {
					dataOptions += "multiline:true,height:66";
				}
				else {
					dataOptions += ", multiline:true,height:66";
				}
			}
			if (columnMeta.isNullable.equals("NO")) {
				if (StrKit.isBlank(dataOptions)) {
					dataOptions += "required:true";
				}
				else {
					dataOptions += ", required:true";
				}
			}
			
			String columnName = org.kungfu.util.StrKit.toCamelCase(columnMeta.name);
			
			ret.append(String.format(formContentTemplate, columnMeta.remarks, columnName , columnName, controlType, dataOptions));
			
		}
		
	}

	
	protected void wirtToFile(List<TableMeta> tableMetas, String fileName) {
		try {
			for (TableMeta tableMeta : tableMetas)
				wirtToFile(tableMeta, fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 若 webPage 文件存在，则不生成，以免覆盖用户手写的代码
	 */
	protected void wirtToFile(TableMeta tableMeta, String fileName) throws IOException {
		File dir = new File(webPageOutputDir + File.separator + tableMeta.modelName.toLowerCase().replaceAll("_", "") );
		if (!dir.exists())
			dir.mkdirs();
		
		String target = webPageOutputDir + File.separator + tableMeta.modelName.toLowerCase().replaceAll("_", "") + File.separator + fileName;
		
		File file = new File(target);
		if (file.exists()) {
			return ;	// 若 page 存在，不覆盖
		}
		
		FileWriter fw = new FileWriter(file);
		try {
			fw.write(tableMeta.modelContent);
		}
		finally {
			fw.close();
		}
	}
}


