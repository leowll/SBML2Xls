package sbml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;

public class ReadWrite {
	public static void main(String[] args) {
		SBMLReader sr = new SBMLReader();
		
		try {
			//读取SBML文件
			SBMLDocument sd = sr.readSBML("recon2.v02.xml");
			Model model = sd.getModel();
			
			//把所有的metabolic放入数据库中
			ListOf<Species> speciesList=model.getListOfSpecies();
			SbmlDAO sDAO = new SbmlDAO();
			System.out.println("Begin inserting data to database...");
			sDAO.insertMetabolics(speciesList);
			System.out.println("Finished inserting.");
			
			//把sbml的数据都写入excel中
			System.out.println("Begin writing data to excel...");
			WriteToExcel wte=new WriteToExcel(model,"species.xls",sDAO);
			wte.writeExcel();
			sDAO.closeConnection();
			System.out.println("Finished!");

			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
}
