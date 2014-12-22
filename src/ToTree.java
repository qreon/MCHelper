/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MCHelper;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * S'occupe de construire un arbre en mémoire à partir d'un fichier XML
 * La structure est un peu particulière : dans un arbre normal, on a une racine, qui connait 
 * ses propres enfants, et les enfants connaissent leur propres enfants également, etc.
 * L'arbre en lui-même ne connait pas ses noeuds et feuilles.
 * Ici, l'arbre construit connait en plus de sa racine tous ses noeuds et feuilles.
 * Les liens de "parenté" restent gérés au niveau des noeuds cependant.
 * Ceci permet de récupérer une feuille en accès aléatoire, pas besoin de parcours ou quoi que ce soit.
 * @author François
 */
public abstract class ToTree {
	private static Document doc;
	private static Element root;
	
	private static List<Element> recipes;
	private static List<Element> resources;
	
	private static void open(URL document)
	{
		SAXBuilder sxb = new SAXBuilder();
		try
		{
		   doc = sxb.build(document);
		   root = doc.getRootElement();
		}
		catch(Exception e)
		{
			System.out.println("Erreur : "+e);
		}
	}
	
	//Récupère tous les produits des recettes du fichier XML,
	//ainsi que toutes les ressources décrites dans celui-ci.
	//(les liste des recettes, de ressources, et des noeuds sont
	//stockées dans des variables de classe pour future réutilisation)
	private static void buildLeaves(Tree t)
	{
		recipes = root.getChildren("Recipe");				//Récupérer les recettes
		Iterator i = recipes.iterator();					//Itérer sur la liste

		while(i.hasNext())
		{
			Element cur = (Element)i.next();				//On récupère une des recettes,
			Element prod = (Element)cur.getChild("Outcome");//le noeud du produit
			String name = prod.getText();					//son nom
			Attribute curQtAttr = prod.getAttribute("qt");	//la quantité créée
			int curQt = 1;
			try {	
				curQt = curQtAttr.getIntValue();			//en tant que int
			} catch (Exception ex) {
				System.out.println("Erreur :" + ex);
			}
			t.addLeave(name, curQt);						//On créé un noeud à son nom et on le stocke
		}
		
		resources = root.getChildren("Resource");			//On récupère maintenant les ressources
		i = resources.iterator();							//Et on itère dessus
		
		while(i.hasNext())
		{
			Element cur = (Element)i.next();				//Pour chacune des ressources,
			String name = cur.getText();					//On récupère son nom
			t.addLeave(name, true);							//Et on lui fait un noeud (en indiquant que c'est une ressource) qu'on stocke
		}
		
		//A la fin de ces boucles, tous les objets du jeu ont dû être recensés :
		//Tous peuvent soit être trouvés naturellement, ou bien fabriqués.
	}
	
	//Créé les liens entres toutes les feuilles et se charge de leur pondération
	private static void buildTree(Tree t)
	{
		Iterator i = recipes.iterator();
		
		while(i.hasNext())
		{
			try
			{
				Element curRec = (Element)i.next();								//On récupère le noeud XML de la recette pioche en bois,
				Element out = curRec.getChild("Outcome");						//le noeud XML du produit pioche en bois,
				String recName = out.getText();									//le nom du produit pioche en bois,

				List<Element> ingredients = curRec.getChildren("Ingredient");	//On fait une liste des ingrédients
				Iterator j = ingredients.iterator();							//et on itère dessus

				//Pour chacun des ingrédients, on veut ajouter le noeud correspondant à son nom
				//dans les enfants de curRec (un ingrédient est un enfant du produit d'une recette).
				while(j.hasNext())
				{
					Element curIng = (Element)j.next();				//On récupère le noeud XML de l'ingrédient bâton,
					String ingName = curIng.getText();				//le nom de l'ingrédient bâton,
					Attribute qtAttr = curIng.getAttribute("qt");	//et la quantité (attribut) de bâton nécessaires pour fabriquer la pioche en bois
					int qt = qtAttr.getIntValue();					//on convertit en int
					t.addChildToLeave(recName, ingName, qt);		//On lie les feuilles
				}
			}
			catch(Exception e)
			{
				System.out.println("Erreur: "+e);
			}
		}
		
		//System.out.println(leaves);
	}
	
	public static Tree prepareTree(URL uri, Tree t)
	{
		open(uri);				//On ouvre le fichier XML et on récupère sa racine
		buildLeaves(t);			//On créé toutes les feuilles
		buildTree(t);			//On lie les feuilles entre elles
		
		for(Entry<String, Node> e : t.getLeavesMap().entrySet())
		{
			if (!e.getValue().hasParent())					//Si la feuille considérée n'a pas de parent => n'intervient dans aucune recette
			{
				t.root().addChild(e.getKey(), 0);			//On ajoute une feuille sous la "racine virtuelle" de l'arbre
			}
		}
		
		return t;
	}
}
