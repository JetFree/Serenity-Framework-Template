package com.project.qa.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.nio.charset.Charset;

public class DragAndDropHTML5Helper {

    private WebDriver webDriver;

    public DragAndDropHTML5Helper(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    //http://stackoverflow.com/questions/33849040/drag-and-drop-testing-in-selenium-on-html5-using-java?lq=1
    //http://stackoverflow.com/questions/29381233/how-to-simulate-html5-drag-and-drop-in-selenium-webdriver
    public void executeDragAndDrop(String dragLocator, String dropLocator) throws InterruptedException, IOException{
        try{

            //http://stackoverflow.com/questions/29381233/how-to-simulate-html5-drag-and-drop-in-selenium-webdriver
            //https://gist.github.com/rcorreia/2362544

            final String JQUERY_LOAD_SCRIPT = "jquery_load_helper.js";
            String jQueryLoader = readFile(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                    + "resources" + File.separator + JQUERY_LOAD_SCRIPT);

            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            js.executeAsyncScript(
                    jQueryLoader /* , http://localhost:8080/jquery-1.7.2.js */);

            //http://stackoverflow.com/questions/29381233/how-to-simulate-html5-drag-and-drop-in-selenium-webdriver
            //"where jquery_load_helper.js contains:"
            String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                    + "resources" + File.separator + "drag_and_drop_helper.js";


            //JQuery can ONLY work with id and css , xpath does NOT work with it.

            StringBuffer buffer = new StringBuffer();
            String line;
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while((line = br.readLine())!=null)
                buffer.append(line);

            String javaScript = buffer.toString();

            javaScript = javaScript + "$('" + dragLocator + "').simulateDragDrop({ dropTarget: '" + dropLocator + "'});";
            js.executeScript(javaScript);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static String readFile(String file) throws IOException {
        Charset cs = Charset.forName("UTF-8");
        FileInputStream stream = new FileInputStream(file);
        try {
            Reader reader = new BufferedReader(new InputStreamReader(stream, cs));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        } finally {
            stream.close();
        }
    }

}
