# nh-logutil
log logutil

联系qq942225169

在业务代码中使用时不必给每个类创建logger对象，直接调用LogUtil的静态方法打印日志
LogUtil的常用静态方法有，info、debug、error
LogUtil的特殊方法trackApi（专门输出报文日志）、trackPoint（专门输出pvuv统计埋点日志）
默认info日志中包括所有基本日志输出，同时单独输出一份error日志，info日志和error日志均按照30天滚动

在业务代码中使用LogUtil样例
```
LogUtil.info("查询订单最新报价参数={}", "1000");
```

与springboot项目整合，使用以下样例代码初始化logutil
```
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;

import com.nh.micro.logutil.LogUtil;

/**
 *
 * @author ninghao
 *
 */
@Configuration
public class LogAutoConfiguration {
	@Autowired
	Environment env;

	@Value("#{logDirStr}")
	private String rootDir;
	@Value("${logutil.startDebug:true}")
	private String startDebug;

	@PostConstruct
	public void init() {
		Boolean debugFlag = Boolean.valueOf(startDebug);
		LogUtil.initLogContext(rootDir, debugFlag);
	}

	@Bean
	public String logDirStr() {
		String dir = env.getProperty("logutil.rootDir");
		if (dir == null || "".equals(dir)) {
			try {

				File path = new File(ResourceUtils.getURL("classpath:").getPath());
				String curPath = path.getParent();
				if (curPath.contains("!")) {
					String[] arr = curPath.split("!");
					String tmpPath = arr[0];
					File tmpFile = new File(tmpPath);
					dir = tmpFile.getParent();
				} else {
					dir = curPath;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (dir != null && dir.startsWith("file:")) {
			dir = dir.substring(5);
		}
		String subdir=env.getProperty("logutil.subDir");
		if(subdir!=null && !"".equals(subdir)){
			dir=dir+File.separator+subdir;
		}
		return dir;

	}
}
```
