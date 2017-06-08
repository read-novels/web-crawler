package com.ztq.webCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;


public class HttpService {
	public static void main(String[] args) {
		String url = "http://m.31xs.com/0/314/5082406.html";
		
		String filePath = "E:\\小说下载\\太古神王.txt";
		StringBuffer novelText = getNovelText(url);
//		String replace = novelText.toString().replace("。", "。\r\n");
		writeToFile(novelText,filePath);
		
	}
	
	private static void writeToFile(StringBuffer novelText, String filePath) {
		File file = new File(filePath);
		try {
			if (file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fileWriter);
			bw.write(novelText.toString());
			bw.flush();
			bw.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static StringBuffer getNovelText(String url) {
		String webSiteName = getWebSiteName(url);
		StringBuffer sb = new StringBuffer();
		String nextUrl = null;
		String page = null;
		do {
			page = getHtmlPageByUrl(url,"GBK");
			String text = getText(page);
//			System.out.println(text);
			sb.append(text);
//			sb.append("\r\n");
			nextUrl = getNextUrl(page);
			url = webSiteName.concat(nextUrl);
			System.out.println(nextUrl);
			System.out.println(url);
		}while(nextUrl.contains(".html"));
//		String string = sb.toString();
//		String replace = string.replace("\r\n\r\n", "\r\n");
//		System.out.println(replace);
//		return new StringBuffer(replace);
		return sb;
	}


	private static String getWebSiteName(String url) {
		boolean contains = url.contains("http://");
		String fullName = null;
		if (contains) {
			String replace = url.replace("http://", "");
			String[] split = replace.split("/");
			if (split.length > 0) {
				String webSite = split[0];
				fullName = "http://".concat(webSite);
			}
		}
		return fullName;
	}

	private static String getNextUrl(String page) {
		Document parse = Jsoup.parse(page);
		Elements elementsByClass2 = parse.getElementsByClass("am-btn-default");
		Iterator<Element> iterator = elementsByClass2.iterator();
		Element next = null;
		while (iterator.hasNext()) {
			next = iterator.next();
			if (next.text().contains("下一章")) {
				break;
			}
		}
		if (next != null) {
			return next.attr("href");
		}
		return null;
	}

	private static String getText(String page) {
		StringBuffer sb = new StringBuffer();
		Document parse = Jsoup.parse(page);
		Elements title = parse.getElementsByTag("h3");
		sb.append("\t" + title.text());
		sb.append("\r\n");
		Elements elementsByClass = parse.getElementsByClass("am-article-bd");
		Iterator<Element> iterator = elementsByClass.iterator();
		Element next = iterator.next();
		List<Node> childNodes = next.childNodes();
		if (childNodes != null && !childNodes.isEmpty()) {
			for (Node node : childNodes) {
				if (node instanceof Element) {
					sb.append("\r\n");
				}
				if (node instanceof TextNode) {
					String replace = ((TextNode) node).text().replace(" ", "");
					sb.append(replace);
				}
			}
		}
			
//		String text = elementsByClass.text();
//		String replace = text.replace(" ", "");
//		sb.append(replace);
		return sb.toString();
	}

	/**
	 * 根据url和解码返回字符串页面
	 * @param url
	 * @param uncode
	 * @return
	 */
	private static String getHtmlPageByUrl(String url,String uncode) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String string2 = null;
		try {
			CloseableHttpResponse execute = httpClient.execute(httpGet);
			string2 = EntityUtils.toString(execute.getEntity(),uncode);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return getHtmlPageByUrl(url, uncode);
		} catch (IOException e) {
			e.printStackTrace();
			return getHtmlPageByUrl(url, uncode);
		}
		return string2;
	}
}
