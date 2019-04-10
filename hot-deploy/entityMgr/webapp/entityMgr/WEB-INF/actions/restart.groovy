import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.start.Start;

def cmd="cmd /c start \"\" \""+System.getProperty("user.dir")+"\\run.bat\"";
println cmd
Process ps = Runtime.getRuntime().exec(cmd);
response=response as HttpServletResponse 
response.writer.print("ok")
return "success"