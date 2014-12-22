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
 * Classe représentant un arbre.
 * C'est un arbre particulier car il connait tous ses noeuds et feuilles, et pas seulement sa racine.
 * Cela permet un accès aléatoire rapide à n'importe quel élément.
 * Le lien de "parenté" entre les noeuds est toujours géré au niveau des noeuds.
 * @author François
 */
public class Tree {
	//L'arbre est constitué d'une racine et du reste de ses noeuds.
	private Node root;
	private Map<String, Node> leaves;
	
	public Tree()
	{
		root = new Node("virtualNode");
		leaves = new HashMap<>();
	}
	
	public Node root()
	{
		return root;
	}
	
	public String toString()
	{
		String res = root.toString() + "\n";
		for(Entry<String, Node> e : leaves.entrySet())
		{
			res += e.getValue() + "\n";
		}
		
		return res;
	}
	
	//Renvoie les noeuds de l'arbre
	public Map<String, Node> getLeavesMap()
	{
		return leaves;		
	}
	
	//Ajoute une feuille (un noeud en fait)
	public void addLeave(String name, boolean isResource)
	{
		leaves.put(name, new Node(name, isResource));
	}
	
	public void addLeave(String name, int qt)
	{
		leaves.put(name, new Node(name, qt));
	}
	
	//Méthode incroyablement pratique qui permet de récupérer rapidement un noeud.
	//Dieu bénisse les HashMap.
	public Node getLeave(String name)
	{
		return leaves.get(name);
	}
	
	//Créé un lien de parenté entre deux noeuds, avec une pondération.
	public void addChildToLeave(String recName, String ingName, double qt)
	{
		leaves.get(recName).addChild(ingName, qt);
		leaves.get(ingName).setParent(true);
	}
}
