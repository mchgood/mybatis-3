package org.apache.ibatis.parsing.tangkc;

import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


/**
 * @author tangkc
 * @description
 * @date 2020/10/8 13:58
 */
public class DOMTest {

  private String resource = "src/test/java/org/apache/ibatis/parsing/tangkc/inventory.xml";

  @Test
  public void dOMTest() throws Exception{
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    // 开启验证
    documentBuilderFactory.setValidating(true);
    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setCoalescing(false);
    documentBuilderFactory.setExpandEntityReferences(true);
    // 创建 DocumentBuilder
    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
    builder.setErrorHandler(new ErrorHandler() {
      @Override
      public void warning(SAXParseException exception) throws SAXException {
        System.out.println(exception.getMessage());
      }

      @Override
      public void error(SAXParseException exception) throws SAXException {
        System.out.println(exception.getMessage());
      }

      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println(exception.getMessage());
      }
    });

    // 将文档加载到一个 Document 对象中
    Document doc = builder.parse(resource);
    // 创建 XPathFactory
    XPathFactory factory = XPathFactory.newInstance();
    // 创建 XPath 对象
    XPath xPath = factory.newXPath();
    // 编译 XPath 表达式
    XPathExpression expr = xPath.compile("//book[author='Neal Stephenson']/title/text()");
    // 第一个参数指定了 XPath 表达式进行查询的上下文节点，也就是在指定节点下查找符合 XPath 的节点
    // 第二个参数指定了 XPath 表达式的返回类型 。
    Object result = expr.evaluate(doc, XPathConstants.NODESET);
    System.out.println("查询作者为 Neal Stephenson 的图书的标题：");
    NodeList nodes = (NodeList)result;
    for (int i = 0; i < nodes.getLength(); i++) {
      System.out.println(nodes.item(i).getNodeValue());
    }

    System.out.println("查询 1997 年之后的图书的标题");
    nodes = (NodeList)xPath.evaluate("//book[@year>1997]/title/text()",doc,XPathConstants.NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      System.out.println(nodes.item(i).getNodeValue());
    }

    System.out.println("查询 1997 年之后的图书的属性和标题：");
    nodes = (NodeList)xPath.evaluate("//book[@year>1997]/title/text()|//book[@year>1997]/@*",doc,XPathConstants.NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      System.out.println(nodes.item(i).getNodeValue());
    }
  }

  @Test
  public void xpathParserTest() throws Exception{
    String str = "<inventory>\n" +
      "  <book year=\"2000\">\n" +
      "    <title>Snow Crash</title>\n" +
      "    <author>Neal Stephenson</author>\n" +
      "    <publisher>Spectra</publisher>\n" +
      "    <isbn>0553380958</isbn>\n" +
      "    <price>14.95</price>\n" +
      "  </book>\n" +
      "\n" +
      "  <book year=\"2005\">\n" +
      "    <title>Burning Tower</title>\n" +
      "    <author>Larry Niven</author>\n" +
      "    <author>Jerry Pournelle</author>\n" +
      "    <publisher>Pocket</publisher>\n" +
      "    <isbn>074341691</isbn>\n" +
      "    <price>5.99</price>\n" +
      "  </book>\n" +
      "\n" +
      "  <book year=\"1995\">\n" +
      "    <title>Zodiac</title>\n" +
      "    <author>Neal Stephenson</author>\n" +
      "    <publisher>Spectra</publisher>\n" +
      "    <isbn>0553573862</isbn>\n" +
      "    <price>7.50</price>\n" +
      "  </book>\n" +
      "</inventory>";
    try (InputStream inputStream = new ByteArrayInputStream(str.getBytes())){
      XPathParser parser = new XPathParser(inputStream, false);
      Float price = parser.evalFloat("/inventory/book/price");
      XNode xNode = parser.evalNode("//book[@year>1997]/title/text()");
      List<XNode> xNodes = parser.evalNodes("//book");
      for (XNode node : xNodes) {
        String name = node.getName();
        String stringBody = node.getStringBody();
        String year = node.getStringAttribute("year");
        System.out.println(name.toString());
        System.out.println(year.toString());
      }
    }
  }
}
