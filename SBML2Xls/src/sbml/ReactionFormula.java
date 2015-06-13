package sbml;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;

public class ReactionFormula {
	private Reaction reaction;
	private SbmlDAO sDAO;

	/**
	 * 构造函数
	 * 
	 * @param reaction
	 *            反应
	 * @param sDAO
	 *            进行数据库操作的DAO
	 */
	public ReactionFormula(Reaction reaction, SbmlDAO sDAO) {
		this.reaction = reaction;
		this.sDAO = sDAO;
	}

	/**
	 * 得到反应方程式
	 * 
	 * @param reactantList
	 *            反应物的list
	 * @param productList
	 *            生成物的list
	 * @param dao
	 *            进行数据库操作的DAO
	 * @return
	 */
	public String getReactionFormula() {
		ListOf<SpeciesReference> reactantList = this.reaction.getListOfReactants();
		ListOf<SpeciesReference> productList = this.reaction.getListOfProducts();
		String formula = "";
		// 反应的前半部分
		String formerPart = addPlus(reactantList, this.sDAO);
		// 反应的后半部分
		String lastPart = addPlus(productList, this.sDAO);
		if (!formerPart.equals("") && !lastPart.equals("")) {
			formula = formerPart + " = " + lastPart;
		} else {
			formula = "";
		}
		return formula;
	}

	/**
	 * 在反应物或者生成物中间加上加号
	 * 
	 * @param list
	 *            反应物或者生成物的list
	 * @param dao
	 *            进行数据库操作的DAO
	 * @return 返回方程式的前半部分或者后半部分
	 */
	private static String addPlus(ListOf<SpeciesReference> list, SbmlDAO dao) {
		String part = "";
		for (SpeciesReference sr : list) {
			String species = sr.getSpecies();
			int stoichiometry = (int) sr.getStoichiometry();
			String name = dao.getMetabolicName(species);
			if (list.indexOf(sr) == list.size() - 1) {
				part = part + stoichiometry + " " + name;
			} else {
				part = part + stoichiometry + " " + name + " + ";
			}
		}
		return part;
	}
}
