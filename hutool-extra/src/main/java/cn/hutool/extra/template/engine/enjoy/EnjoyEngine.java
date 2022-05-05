package cn.hutool.extra.template.engine.enjoy;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.id.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;
import cn.hutool.extra.template.TemplateEngine;
import com.jfinal.template.source.FileSourceFactory;

import java.io.File;

/**
 * Enjoy库的引擎包装
 *
 * @author looly
 * @since 4.1.10
 */
public class EnjoyEngine implements TemplateEngine {

	private com.jfinal.template.Engine engine;
	private ResourceMode resourceMode;

	// --------------------------------------------------------------------------------- Constructor start

	/**
	 * 默认构造
	 */
	public EnjoyEngine() {}

	/**
	 * 构造
	 *
	 * @param config 模板配置
	 */
	public EnjoyEngine(final TemplateConfig config) {
		init(config);
	}

	/**
	 * 构造
	 *
	 * @param engine {@link com.jfinal.template.Engine}
	 */
	public EnjoyEngine(final com.jfinal.template.Engine engine) {
		init(engine);
	}
	// --------------------------------------------------------------------------------- Constructor end

	@Override
	public TemplateEngine init(TemplateConfig config) {
		if(null == config){
			config = TemplateConfig.DEFAULT;
		}
		this.resourceMode = config.getResourceMode();
		init(createEngine(config));
		return this;
	}

	/**
	 * 初始化引擎
	 * @param engine 引擎
	 */
	private void init(final com.jfinal.template.Engine engine){
		this.engine = engine;
	}

	@Override
	public Template getTemplate(final String resource) {
		if(null == this.engine){
			init(TemplateConfig.DEFAULT);
		}
		if (ObjUtil.equals(ResourceMode.STRING, this.resourceMode)) {
			return EnjoyTemplate.wrap(this.engine.getTemplateByString(resource));
		}
		return EnjoyTemplate.wrap(this.engine.getTemplate(resource));
	}

	/**
	 * 创建引擎
	 *
	 * @param config 模板配置
	 * @return {@link com.jfinal.template.Engine}
	 */
	private static com.jfinal.template.Engine createEngine(final TemplateConfig config) {
		final com.jfinal.template.Engine engine = com.jfinal.template.Engine.create("Hutool-Enjoy-Engine-" + IdUtil.fastSimpleUUID());
		engine.setEncoding(config.getCharsetStr());

		switch (config.getResourceMode()) {
			case STRING:
				// 默认字符串类型资源:
				break;
			case CLASSPATH:
				engine.setToClassPathSourceFactory();
				engine.setBaseTemplatePath(config.getPath());
				break;
			case FILE:
				engine.setSourceFactory(new FileSourceFactory());
				engine.setBaseTemplatePath(config.getPath());
				break;
			case WEB_ROOT:
				engine.setSourceFactory(new FileSourceFactory());
				final File root = FileUtil.file(FileUtil.getWebRoot(), config.getPath());
				engine.setBaseTemplatePath(FileUtil.getAbsolutePath(root));
				break;
			default:
				break;
		}

		return engine;
	}
}
