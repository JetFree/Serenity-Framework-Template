package com.project.qa.teamcity;

import au.com.bytecode.opencsv.CSVReader;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import us.monoid.web.Resty;
import us.monoid.web.XMLResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: Evgeniy
 */
public class TeamCity {
    protected static final Logger logger = LoggerFactory.getLogger(TeamCity.class);
    private static String url;
    private static String buildType;
    private static String login;
    private static String pass;

    public TeamCity() {
        Properties properties = new Properties();
        try {
            logger.info(System.getProperty("user.dir"));
            properties.load(new FileInputStream("." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "teamcity.properties"));
        } catch (IOException e) {
            logger.error("Can't load 'teamcity.properties'");
        }
        url = System.getProperty("tc.server", properties.getProperty("tc.server"));
        login = System.getProperty("tc.login", properties.getProperty("tc.login"));
        pass = System.getProperty("tc.pass", properties.getProperty("tc.pass"));
        buildType = System.getProperty("build.type", properties.getProperty("build.type"));
    }

    public TeamCity openSSHTunnel() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/ssh.tunnel.properties"));
        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        Session session = jsch.getSession(properties.getProperty("login"), properties.getProperty("host"));
        session.setPassword(properties.getProperty("password"));
        session.setPortForwardingL(Integer.parseInt(properties.getProperty("lport")),
                properties.getProperty("remote.host"),
                Integer.parseInt(properties.getProperty("rport")));
        session.setConfig(config);
        try {
            session.connect((int) TimeUnit.SECONDS.toMillis(10));
        } catch (Exception e) {/**/}
        return this;
    }

    public Result getLatestBuildResults() {
        Result result = new Result();
        try {
            Resty resty = new Resty();
            resty.authenticate(url, login, pass.toCharArray());
            XMLResource resource = resty.xml(url + "/app/rest/builds/buildType:" + buildType + ",lookupLimit:1");
            logger.info(resource.toString());
            Boolean hasNext = true;
            String testOccurrences = url + resource.get("//testOccurrences/@href").item(0).getNodeValue();
            while (hasNext) {
                XMLResource results = resty.xml(testOccurrences);
                logger.info(results.toString());
                NodeList nodeList = results.get("//testOccurrences/@nextHref");
                hasNext = (nodeList.getLength() > 0 && (testOccurrences = url + nodeList.item(0).getNodeValue()) != null);
                nodeList = results.get("//testOccurrence");
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NamedNodeMap attributes = nodeList.item(i).getAttributes();
                        result.addResult(attributes.getNamedItem("name").getNodeValue(), attributes.getNamedItem("status").getNodeValue());
                    }
                }
            }
        } catch (Exception e) {/**/}
        return result;
    }

    public Result getLastNBuildResults(int numberOfLastBuilds) {
        List<String> testOccurrencesBuildList = new ArrayList<>();
        Result result = new Result();
        try {
            Resty resty = new Resty();
            resty.authenticate(url, login, pass.toCharArray());
            XMLResource resource = resty.xml(url + "/app/rest/builds/buildType:" + buildType + ",lookupLimit:1");
            logger.info(resource.toString());
            String urlTestOccurrences = resource.get("//testOccurrences/@href").item(0).getNodeValue();
            testOccurrencesBuildList.add(url + urlTestOccurrences);
            Pattern p = Pattern.compile("id:(\\d+)");
            Matcher matcher = p.matcher(testOccurrencesBuildList.get(0));
            if (!matcher.find()) {
                throw new RuntimeException("Can't parse build id in URL: " + p.toString());
            }
            long buildId = Long.parseLong(matcher.group(1));
            for (int i = 1; i < numberOfLastBuilds; i++) {
                testOccurrencesBuildList.add((url + resource.get("//testOccurrences/@href").item(0).getNodeValue()).replaceAll("id:(\\d*)", "id:" + --buildId));
            }
            for (String buildTestsResultsUrl : testOccurrencesBuildList) {
                Boolean hasNext = true;
                while (hasNext) {
                    XMLResource results = resty.xml(buildTestsResultsUrl);
                    logger.info(results.toString());
                    NodeList nodeList = results.get("//testOccurrences/@nextHref");
                    hasNext = (nodeList.getLength() > 0 && (buildTestsResultsUrl = url + nodeList.item(0).getNodeValue()) != null);
                    nodeList = results.get("//testOccurrence");
                    if (nodeList != null) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            NamedNodeMap attributes = nodeList.item(i).getAttributes();
                            result.addResult(attributes.getNamedItem("name").getNodeValue(), attributes.getNamedItem("status").getNodeValue());
                        }
                    }
                }
            }
        } catch (Exception e) {/**/}
        return result;
    }

    public Result getResultsFromCSV(String filePath) {
        Result results = new Result();
        try {
            CSVReader reader = new CSVReader(new FileReader(new File(filePath)));
            List<String[]> lines = reader.readAll();
            for (String[] test : lines.subList(1, lines.size())) {
                results.addResult(test[1], test[2]);
            }
        } catch (Exception e) {/**/}
        return results;
    }

    public void printLatestBuildResults() {
        Set<Class> classes = null;
        List<String> classNames = new ArrayList<String>();
        try {
            classes = this.getLatestBuildResults().getFailedTestClasses();
        } catch (Exception e) {/**/}
        if (classes != null)
            for (Class failedTestClass : classes) {
                classNames.add(failedTestClass.getName() + ".class,");
            }
        Collections.sort(classNames);
        for (String name : classNames) {
            System.out.println(name);
        }
        System.out.println("/_______________________________________/");
    }

    public Result getBuildResults(Integer id) {
        Result result = new Result();
        try {
            Resty resty = new Resty();
            resty.authenticate(url, login, pass.toCharArray());
            XMLResource resource = resty.xml(url + "/app/rest/builds/id:" + id);
            Boolean hasNext = true;
            String testOccurrences = url + resource.get("//testOccurrences/@href").item(0).getNodeValue();
            while (hasNext) {
                XMLResource results = resty.xml(testOccurrences);
                NodeList nodeList = results.get("//testOccurrences/@nextHref");
                hasNext = (nodeList.getLength() > 0 && (testOccurrences = url + nodeList.item(0).getNodeValue()) != null);
                nodeList = results.get("//testOccurrence");
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NamedNodeMap attributes = nodeList.item(i).getAttributes();
                        result.addResult(attributes.getNamedItem("name").getNodeValue(), attributes.getNamedItem("status").getNodeValue());
                    }
                }
            }
        } catch (Exception e) {/**/
            int i = 0;
        }
        return result;
    }
}
