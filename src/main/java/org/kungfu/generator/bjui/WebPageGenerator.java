package org.kungfu.generator.bjui;

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
	protected String indexPageBeginPartTemplate =
			"<#include \"/common/template.html\" />%n" +
			"<@layout actionUrl=\"%s/\">%n" +  
			"\t<div class=\"bjui-pageHeader\">%n" +
			"\t\t<form data-toggle=\"validate\" action=\"%s\" method=\"post\">%n" +
			"\t\t\t<div class=\"bjui-searchBar\">%n" +
			"\t\t\t\t<div class=\"btn-group\" role=\"group\">%n" +
			"\t\t\t\t\t<a class=\"btn btn-blue\" href=\"%s/edit/0\" data-toggle=\"navtab\" data-id=\"%s-add\" data-title=\"添加信息\" ><i class=\"fa fa-plus\"></i> 添加</a>%n" +
			"\t\t\t\t\t<a class=\"btn btn-green\" href=\"%s/edit/{#bjui-selected}\" data-toggle=\"navtab\" data-id=\"%s-edit\" data-title=\"修改信息\" ><i class=\"fa fa-edit\"></i> 编辑</a>%n" +
			"\t\t\t\t\t<button type=\"button\" class=\"btn btn-red\" onclick=\"doajax(this,true,'%s/delete/{#bjui-selected}', '确定要删除选中项吗？')\"><i class=\"fa fa-remove\"></i> 删除</button>%n" +
			"\t\t\t\t</div>%n" +
			"\t\t\t\t<div class=\"alert alert-info search-inline\"><i class=\"fa fa-info-circle\"></i> 单击选中行，支持批量删除</div>%n" +
			"\t\t\t</div>%n" +
			"\t\t</form>%n" +
			"\t</div>%n" +
			"\t<div class=\"bjui-pageContent\">%n" +
			"\t\t<table class=\"table table-striped\" data-selected-multi=\"ture\">%n" +
			"\t\t\t<thead>%n" +
			"\t\t\t\t<tr>%n";
	protected String indexPageListHeaderTemplate =
			"\t\t\t\t\t<th title=\"%s\">%s</th>%n";
	protected String indexPageMidPartTemplate =
			"\t\t\t\t</tr>%n" +
			"\t\t\t</thead>%n" +
			"\t\t\t<tbody>%n" +
			"\t\t\t\t<#list page.getList() as item>%n" +
			"\t\t\t\t\t<tr data-id=\"${item.id}\">%n" ;
	 protected String indexPageListContentTemplate =
			"\t\t\t\t\t\t<td>%s</td>%n";  
	 protected String indexPageEndPartTemplate =
			"\t\t\t\t\t</tr>%n" +
			"\t\t\t\t</#list>%n" +
			"\t\t\t</tbody>%n" +
			"\t\t</table>%n" +
			"\t</div>%n" +
			"</@layout>%n" +
			"<script type=\"text/javascript\">%n" +
			"\t// some script white here ...%n" +
			"</script>%n";
	 
	// index.html template
	protected String editPageBeginPartTemplate =
			"<#include \"/common/template.html\" />%n" +
			"<div class=\"bjui-pageContent\">%n" +
			"\t<form method=\"post\" action=\"%s/save\" data-toggle=\"validate\">%n" +
			"\t\t<input type=\"hidden\" name=\"id\" value=\"${%s.id}\">%n" +
			"\t\t<table class=\"table table-condensed table-hover\">%n" +
			"\t\t\t<tbody>%n";
	
	protected String trBeginTemplate = "\t\t\t\t<tr>%n" ;
	protected String tdBeginTemplate = "\t\t\t\t\t<td>%n";
	protected String tdColspan2BeginTemplate = "\t\t\t\t\t<td colspan=\"2\">%n";
	// form label
	protected String formLabelTemplate = "\t\t\t\t\t\t<label class=\"control-label x100\">%s：</label>%n" ;
	// form control
	protected String formControlTextTemplate =		
			"\t\t\t\t\t\t<input class=\"form-control\" type=\"text\" name=\"%s\" value=\"${%s.%s}\" %s size=\"30\"/>%n";
	protected String formControlTextAreaTemplate =		
			"\t\t\t\t\t\t<textarea name=\"%s\" cols=\"87\" rows=\"1\" data-toggle=\"autoheight\" %s>${%s.%s}</textarea>%n";
	protected String formControlRadioTemplate =		
			"\t\t\t\t\t\t<input type=\"radio\" name=\"%s\" data-toggle=\"icheck\" value=\"true\" data-rule=\"checked\" data-label=\"是&nbsp;&nbsp;\" <#if true==%s.%s>checked</#if>>%n" +
			"\t\t\t\t\t\t<input type=\"radio\" name=\"%s\" data-toggle=\"icheck\" value=\"false\" data-label=\"否\" <#if false==%s.%s>checked</#if>>%n";
	protected String formControlDateTemplate =		
			"\t\t\t\t\t\t<input type=\"text\" name=\"%s\" value=\"${%s.%s}\" data-toggle=\"datepicker\" %s size=\"30\">%n";
	
	protected String tdEndTemplate = "\t\t\t\t\t</td>%n" ;
	protected String trEndTemplate = "\t\t\t\t</tr>%n";
			
	protected String editPageEndPartTemplate =
			"\t\t\t</tbody>%n" +
			"\t\t</table>%n" +
			"\t</form>%n" +
			"</div>%n" +
			"<div class=\"bjui-pageFooter\">%n" +
			"\t<ul>%n" +
			"\t\t<li><button type=\"button\" class=\"btn-close\" data-icon=\"close\">关闭</button></li>%n" +
			"\t\t<li><button type=\"submit\" class=\"btn btn-blue\" data-icon=\"save\">提 交</button></li>%n" +
			"\t</ul>%n" +
			"</div>%n" +
			"<script>%n" +
			"\t$(document).ready(function() {%n" +
			"\t\t// 字典调用%n" +
			"\t\t//ajaxSelected('#control-id', '/dict/somedict', '${somevar}')%n" +
			"\t})%n" +
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
		
		for (TableMeta tableMeta : tableMetas) 
			genEditPageContent(tableMeta);
		wirtToFile(tableMetas, "edit.html");
	}
	
	protected void genIndexPageContent(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genIndexPageBeginPart(tableMeta, ret);
		genIndexPageListHeader(tableMeta, ret);
		genIndexPageMidPart(ret);
		genIndexPageListContent(tableMeta, ret);
		genIndexPageEndPart(ret);
		tableMeta.modelContent = ret.toString();
	}
	
	protected void genEditPageContent(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genEditPageFormBegin(tableMeta, ret);
		genEditPageFormContent(tableMeta, ret);
		geneditPageFormEnd(ret);
		tableMeta.modelContent = ret.toString();
	}
	
	protected void genIndexPageBeginPart(TableMeta tableMeta, StringBuilder ret) {
		String s = tableMeta.modelName.toLowerCase().replaceAll("_", "");
		ret.append(String.format(indexPageBeginPartTemplate, s, s, s, s, s, s, s));
	}
	
	private boolean noFilter(ColumnMeta columnMeta) {  
		if (columnMeta.isPrimaryKey.equalsIgnoreCase("PRI") || columnMeta.name.equals("Explains") || columnMeta.name.equals("Remarks") 
				|| columnMeta.name.equals("Logo_Photo_URL") || columnMeta.name.equals("User_Code") || columnMeta.name.equals("User_Name") 
				|| columnMeta.name.equals("Create_Time") || columnMeta.name.equals("Edit_Time")
				|| columnMeta.name.equals("userCode")  || columnMeta.name.equals("createTime") || columnMeta.name.equals("editTime"))
			return false;
		return true;
	}
	 
	protected void genIndexPageListHeader(TableMeta tableMeta, StringBuilder ret) {
		for (ColumnMeta columnMeta : tableMeta.columnMetas) 
			if (noFilter(columnMeta))
				ret.append(String.format(indexPageListHeaderTemplate, columnMeta.remarks, columnMeta.remarks));
	}
	
	protected void genIndexPageMidPart(StringBuilder ret) {
		ret.append(String.format(indexPageMidPartTemplate));
	}
	
	protected void genIndexPageListContent(TableMeta tableMeta, StringBuilder ret) {
		for (ColumnMeta columnMeta : tableMeta.columnMetas) 
			if (noFilter(columnMeta)) 
				ret.append(String.format(indexPageListContentTemplate, String.format("${item.%s}", columnMeta.type.contains("BIT") ? columnMeta.name + "?c" : columnMeta.name)));
	}
	
	protected void genIndexPageEndPart(StringBuilder ret) {
		ret.append(String.format(indexPageEndPartTemplate));
	}
	
	protected void genEditPageFormBegin(TableMeta tableMeta, StringBuilder ret) {
		String s = tableMeta.name.toLowerCase().replaceAll("_", "");
		ret.append(String.format(editPageBeginPartTemplate, s, StrKit.firstCharToLowerCase(tableMeta.modelName)));
	}
	
	protected void genEditPageFormContent(TableMeta tableMeta, StringBuilder ret) {
		int count = 0;
		for (ColumnMeta columnMeta : tableMeta.columnMetas) {
			if (columnMeta.name.equalsIgnoreCase("id") || columnMeta.name.equals("User_Code") || columnMeta.name.equals("User_Name")  || columnMeta.name.equals("Create_Time") || columnMeta.name.equals("Edit_Time")
					|| columnMeta.name.equals("userCode") || columnMeta.name.equals("user_id")   || columnMeta.name.equals("createTime") || columnMeta.name.equals("editTime") || columnMeta.name.equals("create_at") || columnMeta.name.equals("edit_at"))
				continue;
			
			boolean isTextArea = false;
			if (columnMeta.type.contains("(") && columnMeta.type.contains(")") && org.kungfu.util.StrKit.hasDigit(columnMeta.type))
				isTextArea = Integer.parseInt(columnMeta.type.substring(columnMeta.type.indexOf('(')+1, columnMeta.type.indexOf(')'))) >= 200;
			if (count % 2 == 0 || isTextArea) {
				ret.append(String.format(trBeginTemplate));
			}
			if (isTextArea) 
				ret.append(String.format(tdColspan2BeginTemplate));
			else
				ret.append(String.format(tdBeginTemplate));
			
			ret.append(String.format(formLabelTemplate, columnMeta.remarks));
			String dataRule = "";
			if (columnMeta.isNullable.equals("NO")) {
				dataRule = "data-rule=\"required\"";
			}
			 
			//BIT  DATETIME(19)
			if (columnMeta.type.contains("DATE") || columnMeta.type.contains("TIME")  || columnMeta.type.contains("TIMESTEMP")) {
				if (columnMeta.isNullable.equals("NO")) 
					dataRule = "data-rule=\"required;date\"";
				else 
					dataRule = "data-rule=\"date\"";
				ret.append(String.format(formControlDateTemplate, columnMeta.name, StrKit.firstCharToLowerCase(tableMeta.modelName), columnMeta.name, dataRule));
			}
			else if (columnMeta.type.contains("BIT")) 
				ret.append(String.format(formControlRadioTemplate, columnMeta.name,  StrKit.firstCharToLowerCase(tableMeta.modelName), columnMeta.name, columnMeta.name,  StrKit.firstCharToLowerCase(tableMeta.modelName), columnMeta.name));
			else if (isTextArea)
				ret.append(String.format(formControlTextAreaTemplate, columnMeta.name, dataRule, StrKit.firstCharToLowerCase(tableMeta.modelName), columnMeta.name));
			else
				ret.append(String.format(formControlTextTemplate, columnMeta.name, StrKit.firstCharToLowerCase(tableMeta.modelName), columnMeta.name, dataRule));
			
			ret.append(String.format(tdEndTemplate));
			
			if (count > 0 && count % 2 == 1 || isTextArea) {
				ret.append(String.format(trEndTemplate));
			}
			if (isTextArea) 
				count = 0;
			else
			count++;
		}
		if (count % 2 == 1) {
			ret.append(String.format(trEndTemplate));
		}
	}
	
	protected void geneditPageFormEnd(StringBuilder ret) {
		ret.append(String.format(editPageEndPartTemplate));
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


