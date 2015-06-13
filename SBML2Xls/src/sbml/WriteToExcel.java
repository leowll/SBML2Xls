package sbml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;

public class WriteToExcel {
	private Workbook wb;
	private Model model;
	private String fileName;
	private SbmlDAO sDAO;

	/**
	 * 构造函数
	 * 
	 * @param model
	 *            SBML文件的model
	 * @param fileName
	 *            想要写入的excel文件的文件名
	 */
	public WriteToExcel(Model model, String fileName, SbmlDAO sDAO) {
		this.wb = new HSSFWorkbook();
		this.model = model;
		this.fileName = fileName;
		this.sDAO = sDAO;
	}

	/**
	 * 把数据写入到excel文件中 3个sheet metabolic, compartment, reaction
	 */
	public void writeExcel() {
		ListOf<Species> speciesList = model.getListOfSpecies();
		ListOf<Compartment> compartmentList = model.getListOfCompartments();
		ListOf<Reaction> reactionList = model.getListOfReactions();
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(fileName);
			writeMetaboliteToSheet(speciesList, wb);
			writeCompartmentsToSheet(compartmentList, wb);
			writeReactionsToSheet(reactionList, wb, sDAO);
			wb.write(fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 把所有Metabolic的内容写入到excel中
	 * 
	 * @param list
	 *            Species的list
	 * @param wb
	 *            Excel的 workbook
	 */
	public static void writeMetaboliteToSheet(ListOf<Species> list, Workbook wb) {
		Sheet sheet = wb.createSheet("Metabolite");
		setHeaderCell(0, "ID", sheet, wb);
		setHeaderCell(1, "Metabolite", sheet, wb);
		setHeaderCell(2, "Compartment", sheet, wb);
		for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow(i + 1);
			Cell cell = row.createCell(0);
			cell.setCellValue(list.get(i).getId());
			cell = row.createCell(1);
			cell.setCellValue(list.get(i).getName());
			cell = row.createCell(2);
			cell.setCellValue(list.get(i).getCompartment());
		}
	}

	/**
	 * 把所有Compartment的内容写入到excel中
	 * 
	 * @param list
	 *            Comartment的list
	 * @param wb
	 *            Excel的 workbook
	 */
	public static void writeCompartmentsToSheet(ListOf<Compartment> list, Workbook wb) {
		Sheet sheet = wb.createSheet("Compartment");
		setHeaderCell(0, "ID", sheet, wb);
		setHeaderCell(1, "Name", sheet, wb);
		for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow(i + 1);
			Cell cell = row.createCell(0);
			cell.setCellValue(list.get(i).getId());
			cell = row.createCell(1);
			cell.setCellValue(list.get(i).getName());

		}
	}

	/**
	 * 把所有Reaction的内容写入到excel中
	 * 
	 * @param list
	 *            Reaction的list
	 * @param wb
	 *            Excel的 workbook
	 */
	public static void writeReactionsToSheet(ListOf<Reaction> list, Workbook wb, SbmlDAO sDAO) {
		Sheet sheet = wb.createSheet("Reaction");
		setHeaderCell(0, "ID", sheet, wb);
		setHeaderCell(1, "Name", sheet, wb);
		setHeaderCell(2, "Reversible", sheet, wb);
		setHeaderCell(3, "Formula", sheet, wb);
		int index = 0;
		for (Reaction reaction : list) {
			if (reaction.getListOfReactants().size() != 0 && reaction.getListOfProducts().size() != 0) {
				Row row = sheet.createRow(index + 1);
				Cell cell = row.createCell(0);
				cell.setCellValue(reaction.getId());
				cell = row.createCell(1);
				cell.setCellValue(reaction.getName());
				cell = row.createCell(2);
				cell.setCellValue(reaction.getReversible() ? 1 : 0);
				cell = row.createCell(3);
				ReactionFormula rf = new ReactionFormula(reaction, sDAO);
				cell.setCellValue(rf.getReactionFormula());
				index++;
			}
		}
	}

	/**
	 * 设置每张sheet的第一行
	 * 
	 * @param cellNo
	 *            第几列
	 * @param content
	 *            内容
	 * @param sheet
	 *            excel的sheet
	 * @param wb
	 *            excel的workbook
	 */
	public static void setHeaderCell(int cellNo, String content, Sheet sheet, Workbook wb) {
		Row row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		Cell cell = row.createCell(cellNo);
		CellStyle cs = wb.createCellStyle();
		cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
		cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cs.setFont(font);
		cell.setCellValue(content);
		cell.setCellStyle(cs);
	}
}
