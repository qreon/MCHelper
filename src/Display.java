/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MCHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Classe qui était sensée servir à l'affichage de divers éléments
 * mais qui au final n'a pas servi à grand chose de rare
 * @author François
 */
public abstract class Display {
	
	private static Map<Integer, String> chooseMap;
	
	//Affiche les recettes possibles et les mappe avec des int
	public static String displayRecipes(Tree t)
	{
		String res = "";
		int i = 0;
		
		chooseMap = new HashMap<>();
		//Cette map permettra de retrouver le noeud de la recette
		//choisie grâce à l'entier entré par l'utilisateur
		
		for(Entry<String, Node> e : t.getLeavesMap().entrySet())
		{
			Node n = e.getValue();
			String name = n.name();
			
			if(!n.isResource())
			{
				i++;
				chooseMap.put(i, name);
				res += i + ". " + name + "\n";
			}
		}
		
		return res;
	}
	
	public static Map chooseMap()
	{
		return chooseMap;
	}
}
