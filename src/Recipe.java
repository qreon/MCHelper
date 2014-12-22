/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MCHelper;

import static java.lang.Math.ceil;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe modélisant une recette : l'objet retourné ainsi
 * que tous les objets nécessaires à sa fabrication
 * @author François
 */
public class Recipe {
	private String name;
	private Set<Entry<String, Double>> ingredients;
	private Map<String, Double> total;
	private Map<String, Double> stash;
	private Tree t;
	private boolean debug = false;
	
	public Recipe(Tree t)
	{
		this.t = t;
	}
	
	public void build(Node n)
	{
		boolean recipeBuilt;
		name = n.name();										//On récupère le nom de la recette
		ingredients = n.getChildren();							//On récupère ses ingrédients
		total = new ConcurrentHashMap<>();
		stash = new ConcurrentHashMap<>();		
		//Une concurrent HashMap peut être modifiée en même temps que l'on itère dessus sans nous balancer d'exception
		
		for(Entry<String, Double> e : ingredients)				//On met tous les ingrédients dans une map
		{
			total.put(e.getKey(), e.getValue());
		}
		
		if(debug)
			System.out.println("\nBuilding a "+name+"\n");
	
		recipeBuilt = check();									//On vérifie le contenu de la map
		while(!recipeBuilt)										//Tant que la recette n'est pas terminée
		{			
			ingredients = total.entrySet();						//On récupère les éléments de la map.
			
			for(Entry<String, Double> e : ingredients)			//Pour chaque élément
			{
				Node ing = t.getLeave(e.getKey());
				
				if(debug)
					System.out.println("Current ingredient: "+e.getKey());
	
				if(!ing.isResource() && e.getValue() != 0)		//si ce n'est pas une ressource et qu'il y en a > 0
				{
					if(debug)
						System.out.println("Is not a resource");
	
					double needed = e.getValue();				//Nombre d'éléments voulu
					
					if(debug)
						System.out.println("Needed: "+(int)needed);
	
					double fromStash = 0;						//Nombre d'éléments récupérés dans la pile
					int i;

					//On vérifie d'abord dans la pile s'il y a des trucs à piocher
					if(debug)
						System.out.println("Checking stash content");
	
					fromStash = pickFromStash(ing.name(), needed);
					
					if(debug)
						System.out.println("Got "+(int)fromStash+" from stash");
	
					if(fromStash < needed)						//S'il y en a pas ou pas assez
					{
						needed -= fromStash;					//La nouvelle valeur nécéssaire est la précédente moins la valeur piochée
						
						if(debug)
							System.out.println("Not enough: missing "+(int)needed);
	
						double done = 0;						//On stocke le nombre d'objets créés
						while(done < needed)					//Tant qu'on en a pas fait assez, on en créé
						{
							if(debug)
								System.out.println("Creating a set of "+ing.name());
	
							//Ce bloc ajoute les sous-ingrédients de l'ingrédient considéré, à la map total
							for(Entry<String, Double> subE : ing.getChildren())	//On considère chacun de ses sous-ingrédients
							{
								String subName = subE.getKey();
								double newValue = subE.getValue();

								if(debug)
									System.out.println("Adding "+(int)newValue+" needed "+subName+" to list");
	
								if(total.containsKey(subName))					//Si la map contient déjà ce sous-ingrédient
								{
									//On modifie la valeur de la case existante en lui ajoutant la pondération du sous-ingrédient
									//Il ne faut pas oublier de multiplier par la pondération de l'ingrédient père
									newValue = total.get(subName) + newValue;
									total.put(subName, newValue);
								}
								else											//Sinon, on ajoute l'ingrédient
								{
									total.put(subE.getKey(), newValue);
								}
							}
							done += ing.qt();
	
							if(debug)
								System.out.println("Current "+ing.name()+" production: "+(int)done);
						}
						
						if(debug)
						{
							System.out.println("Enough production !");
							System.out.println("Adding "+(int)(done-needed)+" unneeded "+ing.name()+ " to stash");
						}
						
						//Il est possible qu'on ait créé trop de ces objets, on ajoute le reste à la pile
						if(stash.containsKey(ing.name()))
						{
							double newValue = stash.get(ing.name()) + (done - needed);
							stash.put(ing.name(), newValue);
						}
						else
						{
							stash.put(ing.name(), done - needed);
						}
					}
					
					//Dans tous les cas, désormais, on n'a plus besoin de l'ingrédient : on le pondère à 0
					//Ceci est possible puisque parmi les sous ingrédients qu'on a parcourus précédemment,
					//on est assuré qu'aucun n'a modifié sa pondération auparavant étant donné qu'on travaille
					//avec un arbre = graphe acyclique.					
					total.put(e.getKey(), (double)0);
				}
				else if(debug)
				{
					 if(ing.isResource())
					{
						System.out.println("Is resource, skipping...");
					}
					else if(e.getValue() == 0)
					{
						System.out.println("Need 0, skipping...");
					}
					System.out.println("");
				}
			}
			
			if(debug)
				System.out.println("Checking recipe state...\n");
	
			recipeBuilt = check();
			
			if(!recipeBuilt && debug)
			{
				System.out.println("Elements in the list are not all resources.\nLooping again.\n");
			}
		}
		
		if(debug)
			System.out.println("All elements in the list are resources.\nDone!\n");
		
		round();
	}
	
	//Regarde dans la pile et renvoie le nombre d'éléments de nom name
	//qu'on peut piocher dedans, sachant qu'on en veut qt
	private double pickFromStash(String name, double qt)
	{
		if (stash.containsKey(name))
		{
			double presentVal = stash.get(name);
	
			if(debug)
				System.out.println((int)presentVal+" "+name+" in stash");
			
			if(presentVal >= qt)
			{
				stash.put(name, presentVal - qt);
				return qt;
			}
			else
			{
				stash.put(name, (double)0);
				return presentVal;
			}
		}
		else
		{
			return 0;
		}
	}
	
	private void round()
	{
		for(Entry<String, Double> e : total.entrySet())
		{
			double newValue = ceil(e.getValue());
			total.put(e.getKey(), newValue);
		}
	}
	
	//Renvoie vrai uniquement si la map total contient uniquement
	//des ressources ou des éléments dont la pondération est 0
	private boolean check()
	{		
		for(Entry<String, Double> e : total.entrySet())	//Pour chacun des éléments de la map
		{
			Node n = t.getLeave(e.getKey());			//On récupère la feuille correspondante
			if(!n.isResource())							//Si ce n'est pas une ressource,
			{
				if(e.getValue() != 0)					//et si sa pondération est différente de 0,
				{
					return false;						//On renvoie false
				}
			}
		}
		
		return true;									//Si on arrive jusque là, on peut return true
	}
	
	//toString pour l'affichage minimal des recettes (ressources)
	public String toStringResources()
	{
		String res = "Total resources needed for " + name + " :\n";
		for(Entry<String, Double> e : total.entrySet())
		{
			double value = e.getValue();
			int val = (int)value;
			
			if (val != 0)
			{
				res += "\t" + val + "x " + e.getKey() + "\n";
			}
		}
		
		return res;
	}

	//toString pour l'affichage détaillé des recettes (étapes)
	public String toString()
	{
		String res = "Recipe for " + name + ":\n";
		res += (t.getLeave(name)).displayChildren() + "\n";
		for(Entry<String, Double> e : total.entrySet())
		{
			Node n = t.getLeave(e.getKey());
			if(!n.isResource())
			{
				res += "Recipe for " + n.name() + ":\n";
				res += n.displayChildren() + "\n";
			}
		}
		
		res += toStringResources();
		
		return res;
	}
	
	public void debug()
	{
		debug = true;
	}
	
	public void nodebug()
	{
		debug = false;
	}
	
	public void setdebug(boolean v)
	{
		debug = v;
	}
}