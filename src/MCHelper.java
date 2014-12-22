/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MCHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author François
 */
public class MCHelper {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Tree t = new Tree();
		try
		{
			File f = new File("recipes.xml");
			URI uri = f.toURI();
			URL url = uri.toURL();
			url = escapeInvalidChars(url);
			t = ToTree.prepareTree(url, t);
			//Ici, on a construit l'arbre en mémoire grâce au contenu d'un fichier XML (qui s'appelle impérativement "recipes.xml")
			//Comme les méthodes officielles n'échappent pas la plupart des caractères invalides pour une url,
			//du genre 'ç', 'à', ..., il a fallu faire une fonction crade à la main.
		}
		catch(Exception e)
		{
			System.out.println("Error: "+e);
			return;
			//En cas d'erreur on arrête tout
		}
		
		//On affiche les recettes disponibles et on demande de faire un choix
		System.out.print(Display.displayRecipes(t));
		
		Scanner keyboard = new Scanner(System.in);
		System.out.print("\nChoose a recipe: ");
		int theChosenOne = keyboard.nextInt();
		
		//On récupère ensuite le noeud correspondant à la recette
		String theChosenName = (String)Display.chooseMap().get(theChosenOne);
		Node theChosenNode = t.getLeave(theChosenName);
		
		//Puis on la construit et on l'affiche
		Recipe r = new Recipe(t);
		r.setdebug(false);	//Cette ligne indique s'il faut afficher ou non les phrases de débuggage
		r.build(theChosenNode);
		System.out.println(r);
	}
	
	//Renvoie une URL valide avec des caractère invalides échappés car les méthodes officielles
	//ne peuvent pas le faire elles-mêmes
	public static URL escapeInvalidChars(URL u) throws MalformedURLException
	{
		String p = u.getFile();
		String res = "";
		
		for(char c : p.toCharArray())
		{
			switch(c)
			{
				case 'à': res += "%C3%A0";
					break;
				case 'â': res += "%C3%A2";
					break;
				case 'ä': res += "%C3%A4";
					break;
				case 'ç': res += "%C3%A7";
					break;
				case 'è': res += "%C3%A8";
					break;
				case 'é': res += "%C3%A9";
					break;
				case 'ê': res += "%C3%AA";
					break;
				case 'ë': res += "%C3%AB";
					break;
				case 'î': res += "%C3%AE";
					break;
				case 'ï': res += "%C3%AF";
					break;
				case 'ô': res += "%C3%B4";
					break;
				case 'ö': res += "%C3%B6";
					break;
				case 'ù': res += "%C3%B9";
					break;
				case 'û': res += "%C3%BB";
					break;
				case 'ü': res += "%C3%BC";
					break;
				case 'ÿ': res += "%C3%BF";
					break;
				case 'À': res += "%C3%80";
					break;
				case 'Â': res += "%C3%82";
					break;
				case 'Ä': res += "%C3%84";
					break;
				case 'Ç': res += "%C3%87";
					break;
				case 'È': res += "%C3%88";
					break;
				case 'É': res += "%C3%89";
					break;
				case 'Ê': res += "%C3%8A";
					break;
				case 'Ë': res += "%C3%8B";
					break;
				case 'Î': res += "%C3%8E";
					break;
				case 'Ï': res += "%C3%8F";
					break;
				case 'Ô': res += "%C3%94";
					break;
				case 'Ö': res += "%C3%96";
					break;
				case 'Ù': res += "%C3%99";
					break;
				case 'Û': res += "%C3%9B";
					break;
				case 'Ü': res += "%C3%9C";
					break;
				case 'Ÿ': res += "%C5%B8";
					break;
				default: res += c;
			}
		}
		
		return new URL(u.getProtocol(), u.getHost(), res);
	}
}
