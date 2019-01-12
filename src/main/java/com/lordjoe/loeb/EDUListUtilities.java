package com.lordjoe.loeb;

import com.lordjoe.utilities.FileUtilities;
import com.lordjoe.utilities.Util;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.lordjoe.loeb.State.MARYLAND;
import static com.lordjoe.loeb.State.getUSStates;

/**
 * com.lordjoe.loeb.EDUListUtilities
 * User: Steve
 * Date: 11/12/2018
 */
public class EDUListUtilities {
    public static String[] getEnrollmentPages() {

        State[] usStates = getUSStates();
        String[] ret = new String[usStates.length];
        for (int i = 0; i < usStates.length; i++) {
            State s = usStates[i];
            ret[i] = getStateEnrollment(s);
        }
        return ret;
    }

    public static String getStateEnrollment(State s) {
        String name = Util.capitalize(s.toString());
        try {
            // Build the URI.
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("en.wikipedia.org/wiki")
                    .setPath("/List_of_colleges_and_universities_in_" + name)
                    .build();

            // Init GET request and client.
            HttpGet getRequest = new HttpGet(uri);
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Get the response.
            HttpResponse response = httpClient.execute(getRequest);
            InputStream content = response.getEntity().getContent();
            String text = FileUtilities.readInFile(content);
            return text;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }


    public static List<College> handleStateWikiperiaList(State s) {
        String state = Util.capitalize(s.toString());
        File testFile = new File("states/" + state + ".html");
        String text = null;
        if (testFile.exists()) {
            text = FileUtilities.readInFile(testFile);
        } else {
            text = getStateEnrollment(s);
            FileUtilities.writeFile(testFile, text);
        }
        List<College> colleges = parseWikiperiaList(text);
        return colleges;
    }

    private static List<College> parseWikiperiaList(String text) {
        List<College> ret = new ArrayList<>();
        String[] lines = text.split("\n");
        boolean inTable = false;
        boolean inRow = false;
        College current = null;
        int columnIndex = 0;
        if (true)
            return ret;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!inTable) {
                if (!line.contains("id=\"Public_two-year_community_colleges\""))
                    continue;
                inTable = true;
            } else {
                if (!inRow) {
                    if (line.contains("<tr>")) {
                        inRow = true;
                        continue;
                    }
                } else {
                    if (line.contains("</table>")) {
                        inRow = false;
                        inTable = false;
                        break;
                    }
                    if (line.contains("</tr>")) {
                        columnIndex = 0;

                        if (current != null) {
                            ret.add(current);
                            current = null;
                        }

                        inRow = false;
                        continue;
                    }

                    //   <td><a href="/wiki/Capitol_Technology_University" title="Capitol Technology University">Capitol Technology University</a>
                    if (current == null) {
                        if (line.contains("<td><a ")) {
                            int start = line.indexOf("\">") + 2;
                            int end = line.indexOf("</a>");
                            String name = line.substring(start, end);
                            current = College.getCollege(name);
                            columnIndex = 0;
                            for (int j = i; j < i + 20; j++) {
                                System.out.println(lines[j]);
                            }
                            continue;
                        } else {
                            continue;
                        }
                    } else {
                        if (line.contains("</td>")) {
                            columnIndex++;
                        } else {
                            switch (columnIndex) {
                                case 1:   // <td><a href="/wiki/Towson,_Maryland" title="Towson, Maryland">Towson</a>
                                    int start = line.indexOf("\">") + 2;
                                    int end = line.indexOf("</a>");
                                    String name = line.substring(start, end);
                                    current.setCity(name);
                                    break;
                                case 2:   // <td>1885
                                    int year = Integer.parseInt(line.replace("<td>", ""));
                                    break;
                                case 3:  // <td><sup id="cite_ref-46" class="reference"><a href="#cite_note-46">&#91;46&#93;</a></sup>
                                    break;
                                case 4:   // <td>1885
                                    String replace = line.replace("<td>", "");
                                    replace = line.replace(",", "");
                                    int enrollment = Integer.parseInt(replace);
                                    current.setEnrollment(enrollment);
                                    break;
                                case 5:  // <td><sup id="cite_ref-46" class="reference"><a href="#cite_note-46">&#91;46&#93;</a></sup>
                                    break;
                            }
                        }
                    }

                }

            }

        }
        return ret;

    }


    private static void writeColleges(List<College> colleges) {
        PrintStream out1 = System.out;
        for (College college : colleges) {
            writeCollege(college, out1);
        }
    }

    private static void writeCollege(College college, PrintStream out1) {
        System.out.print(college.name);
        System.out.print("/t");
        System.out.print(college.getCity());
        System.out.print("/t");
        System.out.print(college.getState());
        System.out.print("/t");
        System.out.print(college.getEnrollment());
        System.out.print("/t");
        System.out.print(college.getUrl());
        System.out.print("/t");
        System.out.print(college.getType());
        System.out.print("/t");
    }

    public static void readAndSaveUrls() throws IOException {
        List<College> colleges = new ArrayList<>();
        String text = FileUtilities.readInFile("SchoolsByUrl.tsv");
        String[] split = text.split("\n");
        for (int i = 1; i < split.length; i++) {
            int index = 0;
            String s = split[i];
            String[] cols = s.split("\t");
            College me = College.getCollege(cols[index++]);
            String url = cols[index++];
            url = url.replace("http://", "");
            me.setUrl(url);
            String city = cols[index++];
            me.setCity(city);
            State state = State.fromString(cols[index++]);
            me.setState(state);
            colleges.add(me);
        }
        saveColleges(colleges);
    }

    public static void saveColleges(List<College> colleges) throws IOException {
        File outFile = new File("Colleges.tsv");
        PrintWriter out = new PrintWriter(new FileWriter(outFile));
        out.println(College.HEADER_LINE);
        for (College college : colleges) {
            out.println(college.toTabbedString());
        }
        out.close();
    }

    public static List<College> readColleges(String file) throws IOException {
        List<College> colleges = new ArrayList<>();
        File outFile = new File(file);
        LineNumberReader rdr = new LineNumberReader(new FileReader(outFile));
        String line = rdr.readLine();
        line = rdr.readLine();
        while (line != null) {
            College e = College.fromTabbedString(line);
            colleges.add(e);
            line = rdr.readLine();
        }
        rdr.close();
        return colleges;
    }

    private static void addTop500Enrollment(List<College> colleges, File f) throws IOException {
        Document doc = Jsoup.parse(f, "utf-8");
        Elements datatable = doc.getElementsByClass("datatable");
        for (Element element : datatable) {
            for (Element row : element.select("tr")) {
                String[] columns = new String[32];
                int index = 0;
                for (Element element1 : row.select("td")) {
                    String s = element1.text();
                    columns[index++] = s;
                }
                String name = columns[2];
                String enrollmentStr = columns[3];
                if (name == null || enrollmentStr == null)
                    continue;
                College me = College.getCollege(name);
                enrollmentStr = enrollmentStr.replace(",", "");
                try {
                    int enrollment = Integer.parseInt(enrollmentStr);
                    if (me != null)
                        me.setEnrollment(enrollment);
                } catch (Exception ex) {
                    System.err.println(element);
                }
            }
        }


    }


    public static void addTop500Enrollments() throws IOException {
        List<College> colleges = readColleges("Colleges.tsv");
        List<College> unknowns = readColleges("UnfoundColleges.tsv");
        colleges.addAll(unknowns);
        for (College college : colleges) {
            readCollegeData(college);
        }


    }

    private static String readCollegeData(College college) {
        File saveDir = new File("savedInfo");
        saveDir.mkdirs();
        String child = college.name.replace(" ", "_") + ".html";
        File saveFile = new File(saveDir, child);

        String text = null;
        if (saveFile.exists()) {
            text = FileUtilities.readInFile(saveFile);
        } else {
            text = getCollegeInfo(college);

            if (text != null)
                FileUtilities.writeFile(saveFile, text);
        }
        return text;
    }

    public static String getCollegeInfo(College college) {
        try {
            // Build the URI.
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("www.stateuniversity.com")
                    .setPath("/universities/" + college.getState().getAbbreviation() +
                            "/" + college.name.replace(" ", "_") + ".html")
                    .build();
            System.out.println(uri.toString());
            // Init GET request and client.
            HttpGet getRequest = new HttpGet(uri);
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Get the response.
            HttpResponse response = httpClient.execute(getRequest);
            InputStream content = response.getEntity().getContent();
            String text = FileUtilities.readInFile(content);
            //         if(!text.contains("<title>U.S. University Directory - Top Universities, Edu Search, University Search, College Search</title>\n"))
            //            return null;
            return text;
        } catch (URISyntaxException e) {
            return null;

        } catch (IOException e) {
            return null;

        }

    }

    private static List<File> buildFileList() {
        List<File> ret = new ArrayList<>();
        File f = new File("top 500");
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory())
                continue;
            if (!file.getName().startsWith("College Rankings"))
                continue;
            ret.add(file);

        }
        return ret;
    }


    public static Set<College> getCampusSelectColleges(Map<String, College> unknown) {
        Set<College> ret = new HashSet<>();
        String s = FileUtilities.readInFile("EducatorsList.tsv");
        StringBuilder sb = new StringBuilder();


        String[] lines = s.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            handleEducatorLine(line, ret, unknown, sb);

        }
        FileUtilities.writeFile("AliastList.tsv", sb.toString());

        return ret;
    }

    private static void handleEducatorLine(String line, Set<College> ret, Map<String, College> unknown, StringBuilder sb) {
        String[] cols = line.split("\t");
        State state = State.fromString(cols[1]);
        if (state.isUSState()) {
            int index = 0;
            String city = cols[index++];
            index++;
            String collegeName = cols[index++];
            String email = cols[index++];
            String url = email.substring(email.indexOf("@") + 1);
            College col = College.getKnownCollege(collegeName);

            if (col == null) {
                col = College.getByUrl(url);
                if (col == null) {
                    if (!unknown.containsKey(collegeName)) {
                        College c = College.makeUnregisteredCollege(collegeName, state, city, url);
                        unknown.put(collegeName, c);
                        String x = c.toTabbedString();
                        System.out.println(x);
                        sb.append(x);
                        sb.append("\n");

                    }
                }
            } else
                ret.add(col);
        }

    }

    private static void writeColleges(String fileName, List<College> colleges) throws IOException {
        Collections.sort(colleges);
        PrintWriter out = new PrintWriter(new File(fileName));
        out.println(College.HEADER_LINE);
        int index = 0;
        for (College college : colleges) {
            out.println(college.toTabbedString());
            index++;
        }
        System.out.println("Written " + index + " colleges");
        out.close();
    }

    private static void annotateCollege(College c, String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.contains("var infowindow = new google.maps.InfoWindow")) {
                addAddress(c, lines[i + 1]);
            }
        }
        Document doc = Jsoup.parse(text, "utf-8");
        Elements datatable = doc.getElementsByClass("datatable");
        for (Element element : datatable) {
            String prev_text = "";
            for (Element featured : element.getElementsByClass("featured_data")) {
                String etext = featured.text();
                if (prev_text.toLowerCase().contains("enrollment")) {
                    etext = etext.replace(",", "");
                    int oldEnrollment = c.getEnrollment();
                    try {
                        int enrollment = Integer.parseInt(etext);
                        c.setEnrollment(enrollment);
                        //               System.out.println(c.name + " old " + oldEnrollment + " new " +  enrollment);
                    } catch (NumberFormatException e) {
                        System.out.println(c.name + " old " + oldEnrollment + " new " + etext);
                    }
                }

                prev_text = etext;
            }
        }
    }

    private static void addAddress(College c, String line) {
        if (line.contains("content: '<address><span>")) {
            line = line.replace("content: '<address><span>", "");
            line = line.replace("<\\/span><br /><span>", "\n");
            line = line.replace("<\\/span><br /><span class=", "\n");
            String address = null;
            String phone = null;
            String url = null;
            String[] splits = line.split("\n");
            address = splits[0].trim() + " " + splits[1].trim();
            if (splits.length > 2 && splits[2].startsWith("\\\"phone\\\">p. "))
                phone = splits[2].substring("\\\"phone\\\">p. ".length());
            if (splits.length > 3 && splits[3].startsWith("\\\"website\\\">w.")) {
                url = splits[3].substring("\\\"website\\\">w.".length());
                url = url.replace("<\\/span><\\/address>',", "");
                url = url.replace("<\\/span><\\/address>", "");
                url = url.replace("<\\/a>", "");
                int index = url.indexOf(">www");
                if(index > -1)
                    url = url.substring(index + 1);
                if (url.endsWith("/"))
                    url = url.substring(0, url.length() - 1);
                url = url.trim();
                if(url.startsWith("www."))
                    url = url.substring(4);

            }

            if (address != null)
                c.setAddress(address);
            if (phone != null)
                c.setPhone(phone);
            if (url != null)
                c.setUrl(url);

        }
    }

    private static void annotateColleges() throws IOException {
        List<College> colleges = readColleges("Colleges.tsv");
        List<College> unknowns = readColleges("UnfoundColleges.tsv");
        colleges.addAll(unknowns);
        Collections.sort(colleges);

        College bakersfield_college = College.getCollege("Bakersfield College");
        String text = readCollegeData(bakersfield_college);
        annotateCollege(bakersfield_college, text);


        for (College c : colleges) {
            text = readCollegeData(c);
            annotateCollege(c, text);
            System.out.println(c.name);
        }

        writeColleges("CollegesAnnotated.tsv", colleges);
    }

    public static void main(String[] args) throws IOException {
        List<College> colleges = readColleges("CollegesWithEnrollment.tsv");
        Map<String, College> unknown = new HashMap<>() ;
        Set<College> usedColleges = getCampusSelectColleges(unknown);

        PrintWriter out = new PrintWriter(new FileWriter("StateTotals.tsv")) ;
        out.println("State\tName\tEnrollment");
        int grandTotal = 0;
        State[] usStates = State.getUSStates();
        for (int i = 0; i < usStates.length; i++) {
            State usState = usStates[i];
            List<College> inState = new ArrayList<>();
            for (College usedCollege : usedColleges) {
                if(usedCollege.getState() == usState)
                    inState.add(usedCollege);
            }
            Collections.sort(inState);

            int total = 0;
            out.println(usState.toString());
            for (College college : inState) {
                out.print("\t");
                out.print(college.name);
                out.print("\t");
                int enrollment = college.getEnrollment();
                total += enrollment;
                out.print(enrollment);
                out.print("\n");
             }
            out.println("total\t\t" + total);
            grandTotal += total;
            out.println();
        }
        out.println();
        out.println();
        out.println("grand total\t\t" + grandTotal);
        out.close();
    }




}
