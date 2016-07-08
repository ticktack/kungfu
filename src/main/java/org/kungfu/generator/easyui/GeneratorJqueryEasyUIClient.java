package org.kungfu.generator.easyui;

import javax.sql.DataSource;

import org.kungfu.generator.BaseModelGenerator;
import org.kungfu.generator.MappingKitGenerator;
import org.kungfu.generator.ModelGenerator;
import org.kungfu.generator.RoutesGenerator;
import org.kungfu.generator.easyui.ControllerGenerator;
import org.kungfu.generator.easyui.Generator;
import org.kungfu.generator.easyui.ModelAPIGenerator;
import org.kungfu.generator.easyui.ServiceGenerator;
import org.kungfu.generator.easyui.WebPageGenerator;

/**
 * created by xiaofeixiang on 2016/7/7 10:38:18.
 */
public class GeneratorJqueryEasyUIClient {

	/**
	 * @param dataSource					// 数据源
	 * @param baseModelPackageName   // base model 所使用的包名
	 * @param baseModelOutputDir		// base model 文件保存路径
	 * @param modelPackageName		// model 所使用的包名 (MappingKit 默认使用的包名)
	 * @param webPageOutputDir         // web page dir
	 * @param prefixes						// 设置需要被移除的表名前缀用于生成modelName。
	 * @param excludedTable				// 添加不需要生成的表名
	 */
	public static void generator(DataSource dataSource, String baseModelPackageName, String baseModelOutputDir, String modelPackageName, String webPageOutputDir,
			String[] prefixes, String[] excludedTable) {
		
			// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
			String modelOutputDir = baseModelOutputDir; // + "/..";
			
			// 创建生成器
			//Generator gernerator = new Generator(dataSource, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
			Generator gernerator = new Generator(dataSource,
					new BaseModelGenerator(baseModelPackageName, baseModelOutputDir),
					new ModelGenerator(modelPackageName, baseModelPackageName, modelOutputDir),
					new ModelAPIGenerator(modelPackageName, baseModelPackageName, modelOutputDir),
					new ServiceGenerator(modelPackageName, baseModelPackageName, modelOutputDir),
					new ControllerGenerator(modelPackageName, baseModelPackageName, modelOutputDir),
					new WebPageGenerator(webPageOutputDir));
			
			// 扩展自定义 MappingKitGenerator
			gernerator.setMappingKitGenerator(new MappingKitGenerator(modelPackageName, modelOutputDir, modelPackageName));
			// 扩展自定义 RoutesGenerator
			gernerator.setRoutesGenerator(new RoutesGenerator(modelPackageName, modelOutputDir, modelPackageName));
			
			// 添加不需要生成的表名
			gernerator.addExcludedTable(excludedTable);
			// 设置是否在 Model 中生成 dao 对象
			gernerator.setGenerateDaoInModel(true);
			// 设置是否生成字典文件
			gernerator.setGenerateDataDictionary(true);
			// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
			gernerator.setRemovedTableNamePrefixes(prefixes);
			// 生成
			gernerator.generate();
	}
}
