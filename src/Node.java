/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MCHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Classe modélisant un noeud quelconque.
 * Ce peut être la racine, un noeud, ou une feuille.
 * Il possède quelques propriétés qui permettent de différencier ces cas.
 * @author François
 */
public class Node {
	private String name;
	//Un noeud correspond au produit d'une recette.
	//qt est le nombre de produits obtenus lors de l'exécution de la recette,
	//et children contient le nom ainsi que le nombre d'ingrédients nécessaires
	//à sa fabrication.
	//hasParent détermine si la feuille doit être reliée à la racine de l'arbre ou non lors de sa construction.
	//isResource indique si c'est une ressource, information souvent utilisée.
	private int qt;
	private Map<String, Double> children;
	private boolean hasParent;
	private boolean isResource;

	public Node(String name)
	{
		this.name = name;
		children = new HashMap<>();
		hasParent = false;
		isResource = false;
		qt = 1;
	}

	public Node(String name, boolean isResource)
	{
		this.name = name;
		children = new HashMap<>();
		hasParent = false;
		this.isResource = isResource;
		qt = 1;
	}
	
	public Node(String name, int qt)
	{
		this.name = name;
		children = new HashMap<>();
		hasParent = false;
		isResource = false;
		this.qt = qt;
	}
	
	public String name()
	{
		return name;
	}
	
	//Renvoie la liste des ingrédients nécéssaires à la recette
	public Set<Entry<String,Double>> getChildren()
	{
		return children.entrySet();
	}
	
	//Ajoute un ingrédient à la recette
	public void addChild(String name, double qt)
	{
		children.put(name, qt);
	}
	
	//Renvoie une chaîne indiquant tous les ingrédients nécessaires à la recette
	public String displayChildren()
	{
		String res = "";
		for(Entry<String,Double> e : children.entrySet())
		{
			res += "\t" + (int)((double)e.getValue()) + "x " + e.getKey() + "\n";
		}
		
		return res;
	}
	
	public boolean hasParent()
	{
		return hasParent;
	}
	
	public boolean isResource()
	{
		return isResource;
	}
	
	public void setParent(boolean p)
	{
		hasParent = p;
	}
	
	public int qt()
	{
		return qt;
	}
	
	public String toString()
	{
		return name + " x" + qt + ":\n\tC:" + children + ",\n\tP:" + hasParent + ",\n\tR:" + isResource + ".\n";
	}
}
