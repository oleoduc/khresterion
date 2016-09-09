/**
 * 
 */
package com.khresterion.due.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.FormattingOption;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.RFonts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author ndriama
 *
 */
/**
 * @author ndriama
 *
 */
public class DocxUtility {

  private static final String FONT_MAIN = "Calibri";

  private static final String TR_TAG = "tr";

  private static final String TD_TAG = "td";

  private static final String COLSPAN = "colspan";

  private static final String ROWSPAN = "rowspan";

  private static final String SAUTDEPAGE = "$sautdepage";

  private static final String EMPTY_DOCX = "/META-INF/due/report/empty.docx";

  private static final String SPACE = " ";
  
  /**
   * 
   */
  public DocxUtility() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * todo: split the original text between page break insert excel table
   * 
   * @param input @param ouptut @throws Docx4JException @throws IOException @throws
   */
  public void htmlToWord(File input, File output, boolean hasTable)
      throws Docx4JException, IOException {

    String empty = DocxUtility.class.getResource(EMPTY_DOCX).getFile();
    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(empty));
    org.docx4j.wml.ObjectFactory wmlObjectFactory = org.docx4j.jaxb.Context.getWmlObjectFactory();    
    RFonts rfonts = wmlObjectFactory.createRFonts();
    rfonts.setAscii(FONT_MAIN);
    rfonts.setHAnsi(FONT_MAIN);    
    XHTMLImporterImpl.addFontMapping(FONT_MAIN, rfonts);
    List<String> xhtml = splitOriginalText(IOUtils.toString(new FileInputStream(input)));
    int breakcount = 0;
    for (String xhtmlPart : xhtml) {      
      XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);      
      try {
        List<Object> wmlList = XHTMLImporter.convert(xhtmlPart, null);        
        wordMLPackage.getMainDocumentPart().getContent().addAll(wmlList);
        if (breakcount < xhtml.size() - 1)
          wordMLPackage.getMainDocumentPart().getContent().add(addPageBreak(wmlObjectFactory));
      } catch (Docx4JException e) {
        if (breakcount < xhtml.size() - 1)
          wordMLPackage.getMainDocumentPart().getContent().add(addPageBreak(wmlObjectFactory));
      }
      breakcount++;
    }
    xhtml.clear();

    if (hasTable) {
      File tempFile = new File(output.getAbsolutePath().replace(".docx", ".tmp0.docx"));
      // File tempFile = new File(output.getAbsolutePath());
      wordMLPackage.save(tempFile, Docx4J.FLAG_SAVE_ZIP_FILE);
      try {
        fitAllTables(tempFile, output.getAbsolutePath());
        tempFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
        wordMLPackage.save(output, Docx4J.FLAG_SAVE_ZIP_FILE);
      }

    } else {
      wordMLPackage.save(output, Docx4J.FLAG_SAVE_ZIP_FILE);
    }
    wordMLPackage = null; // ?
  }

 

  /**
   * @param source
   * @param rangeList
   * @return
   */
  public List<String> tableToHtml(String source, String[] rangeList) {
    OPCPackage pkg = null;
    try {
      File sourceFile = new File(source);
      if(!sourceFile.exists()) return null;
      
      pkg = OPCPackage.open(new File(source), PackageAccess.READ);

      XSSFWorkbook workbook = new XSSFWorkbook(pkg);
      List<String> srList = Lists.newArrayList();
      for (String range : rangeList) {
        XSSFName namedRange = workbook.getName(range);
        if (namedRange != null) {
          AreaReference aref =
              new AreaReference(namedRange.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);
          int rowSize = aref.getLastCell().getRow() + 1;
          int colSize = aref.getLastCell().getCol() + 1;
          System.out.println(namedRange.getSheetName() + "range  " + namedRange.getNameName()
              + " row " + rowSize + " col " + colSize);

          XSSFSheet sheet = workbook.getSheet(namedRange.getSheetName());
          Document tableDoc = Jsoup.parseBodyFragment(
              "<div><table style=\"table-layout: fixed; width: 100%;word-wrap:break-word;border-collapse:collapse;border-spacing:0px;border:none\"><tbody></tbody></table><p>&nbsp;</p></div>");

          Element tbody = tableDoc.getElementsByTag("tbody").first();

          for (int rownum = aref.getFirstCell().getRow(); rownum < rowSize; rownum++) {
            Element trow = tbody.appendElement(TR_TAG);

            XSSFRow xrow = sheet.getRow(rownum);
            System.out.println("rownum : "+ rownum + " content " + ((xrow==null)? "null" : "not null"));
            if(xrow == null) xrow = sheet.createRow(rownum);
            StringBuilder rowStyle = new StringBuilder("height:");
            
            rowStyle = rowStyle.append(toStringInteger(xrow.getHeight() * 1.3333 / 20) + "px;");
            /* create cell map */
            Map<Integer, Integer> spanMap = Maps.newHashMap();
            for (int colnum = aref.getFirstCell().getCol(); colnum < colSize; colnum++) {
              XSSFCell cell = xrow.getCell(colnum);
              if (cell == null) {
                spanMap.put(colnum, 1);
              } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                spanMap.put(colnum, -1);
              } else {
                spanMap.put(colnum, 0);
              }
            }
            int cellnum = aref.getFirstCell().getCol();
            int currentIndex = 0;
            while (cellnum < colSize) {
              if (spanMap.get(cellnum).equals(1)) {
                trow.appendElement(TD_TAG);
                cellnum = cellnum + 1;
              } else if (spanMap.get(cellnum).equals(0)) {
                XSSFCell origCell = xrow.getCell(cellnum);
                Element tdata = trow.appendElement(TD_TAG);
                tdata.appendText(getCellValue(origCell));

                int colspan = 1;
                cellnum = cellnum + 1;
                while ((cellnum < colSize) && (spanMap.get(cellnum) != null)
                    ? spanMap.get(cellnum).equals(-1) : false) {
                  /* empty cells can be merged left or with row above */
                  Element tpreviousRow = trow.previousElementSibling();
                  if (tpreviousRow == null) {
                    colspan = colspan + 1;
                  } else {
                    Elements tdlist = tpreviousRow.children();

                    if(cellnum < tdlist.size()){
                    Element above = tdlist.get(currentIndex);
                    if (above.hasAttr(COLSPAN)) {
                      if (Integer.parseInt(above.attr(COLSPAN)) >= 2) {
                        colspan = colspan + 1;
                      } else {
                        above.attr(ROWSPAN, "2");
                      }
                    } else {
                      colspan = colspan + 1;
                    }
                    }else {
                      colspan = colspan + 1;
                    }
                  }
                  cellnum = cellnum + 1;
                }
                tdata.attr("style", getCellStyle(origCell, rowStyle.toString()));
                if (colspan > 1) {
                  tdata.attr(COLSPAN, Integer.toString(colspan));
                }

                currentIndex = currentIndex + 1;

              } else {
                /* row starting with -1 likely to be merged with the row above */
                Element tpreviousRow = trow.previousElementSibling();
                if (tpreviousRow == null) {
                  trow.appendElement(TD_TAG);
                } else {
                  if(tpreviousRow.children() !=null){
                    if(tpreviousRow.children().first()!=null)
                      tpreviousRow.children().first().attr(ROWSPAN, "2");
                  }
                }
                cellnum = cellnum + 1;
              }
            } // end row
            Element tpreviousRow = trow.previousElementSibling();
            if (tpreviousRow == null) {
            } else {
              if(trow.children().size() ==0){
                trow.remove();
                Elements tdatas = tpreviousRow.children();
                for (int cpt = 0; cpt < tpreviousRow.children().size(); cpt++) {
                  tdatas.get(cpt).removeAttr(ROWSPAN);                  
                }
              } else if (tpreviousRow.children().size() >= trow.children().size()
                  && tpreviousRow.children().size() > 6 && trow.children().size() < 4) {
                Elements tdatas = tpreviousRow.children();
                for (int cpt = 3; cpt < tpreviousRow.children().size(); cpt++) {
                  Element col = tdatas.get(cpt);
                    col.attr(ROWSPAN, "2");
                }
                trow.children().last().removeAttr(COLSPAN);
              } else {
                //TODO
              }
            }
          } // end table

          OutputSettings outputSettings = new OutputSettings();
          outputSettings.escapeMode(EscapeMode.xhtml);
          outputSettings.prettyPrint(true);
          outputSettings.syntax(OutputSettings.Syntax.xml);
          tableDoc.outputSettings(outputSettings);

          srList.add(tableDoc.body().html());
          aref = null;
        } else {
          // return null;
        }
      }
      workbook.close();
      pkg.revert();
      /*
       * if (pkg != null) pkg.close();
       */
      return srList;
    } catch (InvalidFormatException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * @param xrow
   * @return
   */
  private int getColSpan(XSSFRow xrow, short startcol, int totalcol) {
    int blankcell = 0;
    int cellnum = startcol;
    while (cellnum < totalcol) {
      XSSFCell cell = xrow.getCell(cellnum);
      if (cell == null) {
        cellnum = cellnum + 1;
        blankcell = blankcell + 1;
      } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
        blankcell = blankcell + 1;
        cellnum = cellnum + 1;
      } else {
        cellnum = cellnum + 1;
      }
    }
    int colspan = (totalcol > blankcell) ? totalcol / (totalcol - blankcell) : totalcol;
    colspan = Math.min(xrow.getLastCellNum() - 1, colspan);

    return colspan;
  }

  /**
   * @param origCell
   * @return
   */
  private String getCellStyle(XSSFCell origCell, String rowStyle) {
    String style = null;
    if (origCell == null) {
      style = StringUtils.EMPTY;
    } else if (origCell.getCellType() == Cell.CELL_TYPE_BLANK) {
      style = StringUtils.EMPTY;
    } else {
      XSSFColor xcolor = origCell.getCellStyle().getFillForegroundColorColor();
      XSSFColor fcolor = origCell.getCellStyle().getFont().getXSSFColor();
      long fontSize = (long) Math
          .floor(Double.valueOf(origCell.getCellStyle().getFont().getFontHeightInPoints() * 1.333));
      String fontcolor = null;
      if (fcolor == null) {
        fontcolor = "000000";
      } else {
        fontcolor = fcolor.getARGBHex() == null ? "000000" : fcolor.getARGBHex().substring(2);
      }
      if (xcolor == null) {
        style = "font-size: " + fontSize + "px;";
      } else {
        if (xcolor.getARGBHex() == null) {
          style = "color: #" + fontcolor + ";font-size: " + fontSize + "px;";
        } else {
          String rgb = xcolor.getARGBHex().substring(2);
          style = "color: #" + fontcolor + ";background-color: #"
              + (rgb.equals(fontcolor) ? "999999" : rgb) + ";font-size: " + fontSize + "px;";
        }
      }
    }
    String borderStyle = null;
    if ((origCell.getCellStyle() == null) ? true
        : (origCell.getCellStyle().getBorderBottom() == CellStyle.BORDER_NONE
            && origCell.getCellStyle().getBorderTop() == CellStyle.BORDER_NONE)) {
      borderStyle = "border:none;";
    } else {
      borderStyle = "border:1px solid #000000;";
    }
    return style + rowStyle + borderStyle + "word-wrap:break-word;";
  }

  /**
   * @param source
   * @param imageUrl
   * @return
   */
  public String imageToHtml(String imageUrl, String altName, String width, String height, String align) {
    StringBuilder imgString = new StringBuilder();

    imgString.append("<img alt=\"" + altName + "\" ");
    imgString.append("src=\"file:///" + imageUrl + "\" ");    
    imgString.append(" width=\"" + width + "\" height=\"" + height + "\" align=\""+ align + "\" />");    
    return imgString.toString();
  }

  /**
   * @param doc
   * @param outputFile
   * @throws IOException
   */
  private void fitAllTables(File source, String outputFile) throws Exception {
    OPCPackage pkg = OPCPackage.open(source, PackageAccess.READ_WRITE);
    XWPFDocument doc = new XWPFDocument(pkg);

    List<XWPFTable> tableList = doc.getTables();
    for (XWPFTable xtable : tableList) {
      CTTblPr pr = xtable.getCTTbl().getTblPr();
      CTTblWidth tblW = pr.getTblW();
      tblW.setW(BigInteger.valueOf(5000));
      tblW.setType(STTblWidth.PCT);
      pr.setTblW(tblW);
      CTJc jc = pr.addNewJc();
      jc.setVal(STJc.CENTER);
      pr.setJc(jc);
      xtable.getCTTbl().setTblPr(pr);
    }
    FileOutputStream out = new FileOutputStream(outputFile);
    doc.write(out);
    doc.close();
    // pkg.close();
    pkg.revert();
    //System.out.println("Fit All Tables done");
  }

  /**
   * @param originalText
   * @return
   */
  private List<String> splitOriginalText(final String originalText) {
    List<String> list = Lists.newArrayList();
    String pagebreak = SAUTDEPAGE;

    int breakpos = originalText.indexOf(pagebreak);
    int startpos = 0;

    while ((breakpos > 0) && (startpos >= 0) && (startpos < originalText.length())) {
      list.add(cleanupFormat(originalText.substring(startpos, breakpos)));
      startpos = breakpos + pagebreak.length() + 4;
      breakpos = originalText.indexOf(pagebreak, startpos);
    }
    if (startpos < originalText.length()) {
      list.add((startpos > 0) ? cleanupFormat(originalText.substring(startpos))
          : cleanupFormat(originalText));
    }
    return list;
  }

  /**
   * cleanup for breakpage
   * 
   * @param input
   * @return
   */
  private String cleanupFormat(String input) {
    StringBuilder sb = new StringBuilder("<div>\n");
    Document jsoupDoc = Jsoup.parseBodyFragment(input);
    OutputSettings outputSettings = new OutputSettings();
    outputSettings.escapeMode(EscapeMode.xhtml);
    outputSettings.prettyPrint(true);
    outputSettings.syntax(OutputSettings.Syntax.xml);
    jsoupDoc.outputSettings(outputSettings);
    sb = sb.append(jsoupDoc.body().html().replaceAll("</p>", "</p>\n"));
    sb = sb.append("\n</div>\n");
    return sb.toString();
  }

  /**
   * @param factory
   * @return
   */
  private Object addPageBreak(org.docx4j.wml.ObjectFactory factory) {
    P para = factory.createP();
    PPr paraprop = factory.createPPr();
    BooleanDefaultTrue bdt = new BooleanDefaultTrue();
    bdt.setVal(true);
    paraprop.setPageBreakBefore(bdt);
    para.setPPr(paraprop);

    return para;
  }

  /**
   * avoid most errors reading a cell
   * 
   * @param origCell
   * @return
   */
  private String getCellValue(XSSFCell origCell) {
    if (origCell == null)
      return StringUtils.EMPTY;
    String value = null;
    DataFormatter formatter = new DataFormatter(Locale.FRANCE);

    if ((origCell.getCellType() == Cell.CELL_TYPE_NUMERIC)) {
      value = formatter.formatCellValue(origCell);
    } else if ((origCell.getCellType() == Cell.CELL_TYPE_FORMULA)) {
      FormulaEvaluator evaluator = new XSSFFormulaEvaluator(origCell.getSheet().getWorkbook());
      value = formatter.formatCellValue(origCell, evaluator) ;
      /*
      String formatString = origCell.getCellStyle().getDataFormatString();
      if(formatString != null) {
        
        int percentIndex = formatString.indexOf("\" ");
        if(percentIndex >= 0){
          formatString =  formatString.substring(percentIndex);
          }
        formatString = formatString.replaceAll("\"", StringUtils.EMPTY);
      }*/
                   
    } else if (origCell.getCellType() == Cell.CELL_TYPE_STRING) {
      value = formatter.formatCellValue(origCell);
    } else if (origCell.getCellType() == Cell.CELL_TYPE_ERROR) {
      value = origCell.getErrorCellString();
    } else if (origCell.getCellType() == Cell.CELL_TYPE_BLANK) {
      value = origCell.getStringCellValue();
    } else {
      value = origCell.getStringCellValue();
    }
    return value;
  }

  /**
   * @param value
   * @return
   */
  private String toStringInteger(double value) {
    double dval = Math.ceil(Double.valueOf(value));

    return Integer.toString((int) dval);
  }

}
