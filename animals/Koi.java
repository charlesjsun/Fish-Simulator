package com.salty.susan.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.salty.susan.FishWorld;

public class Koi extends Animal<Koi> {

	private Coloration primary, secondary; // secondary is spotted coloration on primary/sides of fish; primary is rest of fish; both are expressed only if !isCarpColor
	private Gene carp, carp1, blackSpots;
	
	// randomly generated for each Koi; narrow range of possible Colors
	private static final NamedColor CARP = new NamedColor("carp", generateRandomColor("carp"));
	private static final NamedColor BLACK = new NamedColor("black", generateRandomColor("black"));
	private static final NamedColor WHITE = new NamedColor("white", generateRandomColor("white"));
	private static final NamedColor RED = new NamedColor("red", generateRandomColor("red"));
	private static final NamedColor ORANGE = new NamedColor("orange", generateRandomColor("orange"));
	private static final NamedColor YELLOW = new NamedColor("yellow", generateRandomColor("yellow"));

	private static class KoiGenome
	{
		public Coloration primary, secondary; // secondary is spotted coloration on primary/sides of fish; primary is rest of fish; both are expressed only if !isCarpColor
		public Gene carp, carp1, blackSpots;
		
		public static KoiGenome generateRandomGenome()
		{
			
			/* primary == secondary or secondary/primary
			10% white
			5% yellow
			4% black
			15% red/white
			10% orange/white
			3% yellow/white
			5% black/yellow
			3% black/white
			5% black/orange
			5% black/red
			15% red
			20% orange
			*/
			
			KoiGenome kg = new KoiGenome();
			
			double color = Math.random();
			if (color < 0.1) {
				kg.primary = new Coloration(WHITE, WHITE);
				kg.secondary = new Coloration(WHITE, WHITE);
			} else if (color < 0.15) {
				kg.primary = new Coloration(YELLOW, YELLOW);
				kg.secondary = new Coloration(YELLOW, YELLOW);
			} else if (color < 0.19) {
				kg.primary = new Coloration(BLACK, BLACK);
				kg.secondary = new Coloration(BLACK, BLACK);
			} else if (color < 0.34) {
				kg.primary = new Coloration(WHITE, WHITE);
				kg.secondary = new Coloration(RED, RED);
			} else if (color < 0.44) {
				kg.primary = new Coloration(WHITE, WHITE);
				kg.secondary = new Coloration(ORANGE, ORANGE);
			} else if (color < 0.47) {
				kg.primary = new Coloration(WHITE, WHITE);
				kg.secondary = new Coloration(YELLOW, YELLOW);
			} else if (color < 0.52) {
				kg.primary = new Coloration(YELLOW, YELLOW);
				kg.secondary = new Coloration(BLACK, BLACK);
			} else if (color < 0.55) {
				kg.primary = new Coloration(WHITE, WHITE);
				kg.secondary = new Coloration(BLACK, BLACK);
			} else if (color < 0.6) {
				kg.primary = new Coloration(ORANGE, ORANGE);
				kg.secondary = new Coloration(BLACK, BLACK);
			} else if (color < 0.65) {
				kg.primary = new Coloration(RED, RED);
				kg.secondary = new Coloration(BLACK, BLACK);
			} else if (color < 0.8) {
				kg.primary = new Coloration(RED, RED);
				kg.secondary = new Coloration(RED, RED);
			} else {
				kg.primary = new Coloration(ORANGE, ORANGE);
				kg.secondary = new Coloration(ORANGE, ORANGE);
			}

			kg.carp = new Gene(false, false);
			kg.carp1 = new Gene(false, false);
			kg.blackSpots = new Gene(false, false);
			if (Math.random() < 0.01)
				kg.carp.setMaleAllele(true);
			if (Math.random() < 0.01)
				kg.carp.setFemaleAllele(true);
			if (Math.random() < 0.01)
				kg.carp1.setMaleAllele(true);
			if (Math.random() < 0.01)
				kg.carp1.setFemaleAllele(true);
			if (Math.random() < 0.05)
				kg.blackSpots.setMaleAllele(true);
			if (Math.random() < 0.05)
				kg.blackSpots.setFemaleAllele(true);
			
			return kg;
		}
		
	}
	
	public Koi(FishWorld fishWorld, Vector3 position) { // generates new Koi with randomly selected traits
		this(fishWorld, position, KoiGenome.generateRandomGenome());
	}
	
	public Koi(FishWorld fishWorld, Vector3 position, KoiGenome kg) { // specified traits
		super(fishWorld, position, Koi.class);
		generateRandomSex();
		generateRandomLengths();
		this.primary = kg.primary;
		this.secondary = kg.secondary;
		this.carp = kg.carp;
		this.carp1 = kg.carp1;
		this.blackSpots = kg.blackSpots;
		
		modelInstance.nodes.get(0).parts.get(0).material.set(ColorAttribute.createDiffuse(primary.getExpressedAllele().getColor()));
		modelInstance.nodes.get(1).parts.get(0).material.set(ColorAttribute.createDiffuse(secondary.getExpressedAllele().getColor()));
		modelInstance.nodes.get(2).parts.get(0).material.set(ColorAttribute.createDiffuse(blackSpots.isExpressed() ? BLACK.getColor() : secondary.getExpressedAllele().getColor()));
		
		scale = currentLength * 10;
		
		printInfo();
	}
	
	@Override
	protected void loadModel()
	{
		Model m = fishWorld.getAssetManager().get("models/koi.obj");
		
		modelInstance = new ModelInstance(m);
//		modelInstance.nodes.get(0).parts.get(0).material.set(ColorAttribute.createDiffuse(primary.getExpressedAllele().getColor()));
//		modelInstance.nodes.get(1).parts.get(0).material.set(ColorAttribute.createDiffuse(secondary.getExpressedAllele().getColor()));
//		modelInstance.nodes.get(2).parts.get(0).material.set(ColorAttribute.createDiffuse(blackSpots.isExpressed() ? BLACK.getColor() : secondary.getExpressedAllele().getColor()));
//		//modelInstance.materials.get(0).set(ColorAttribute.createSpecular(0, 0, 0, 1.0f));
		
	}
	
	public void generateRandomLengths() {
		currentLength = generateRandomLength(0.02f, 0.05f);
		matureLength = generateRandomLength(0.254f, 0.3048f);
		maxLength = generateRandomLength(0.55f, 0.66f);
	}
	
	@Override
	public void dispose()
	{
	}
	
	public void update(float dt)
	{
		super.update(dt);
		rotation += Math.sin(fishWorld.getElapsedTime() * velocity.len()) * 15;
		//System.out.println(this + ": " + sex + " " + timeToReproduction);
	}

	public Sex getSex() {
		return sex;
	}

	public NamedColor getExpressedPrimary() {
		if (carp.isExpressed() || carp1.isExpressed())
			return CARP;
		return primary.getExpressedAllele();
	}

	public NamedColor getExpressedSecondary() {
		if (carp.isExpressed() || carp1.isExpressed())
			return CARP;
		
		Color primaryC = primary.getExpressedAllele().getColor();
		Color secondaryC = secondary.getExpressedAllele().getColor();
		float totalP = primaryC.r + primaryC.g + primaryC.b;
		float totalS = secondaryC.r + secondaryC.g + secondaryC.b;
		if (totalP < totalS)
			return primary.getExpressedAllele(); // darker primary overrides secondary in phenotype but not genotype
		return secondary.getExpressedAllele();
	}

	public static Color generateRandomColor(String colorName) {
		if (colorName.equals("carp"))
			return generateRandomColor(219, 228, 192, 199, 123, 139);
		else if (colorName.equals("black"))
			return generateRandomColor(15, 37, 10, 46, 6, 51);
		else if (colorName.equals("white"))
			return generateRandomColor(225, 246, 218, 240, 225, 242);
		else if (colorName.equals("yellow"))
			return generateRandomColor(249, 255, 178, 223, 20, 91);
		else if (colorName.equals("orange"))
			return generateRandomColor(254, 255, 93, 138, 3, 9);
		else if (colorName.equals("red"))
			return generateRandomColor(233, 254, 4, 36, 0, 9);
		else
			return new Color(1,	1, 1, 1);
	}

	//	public void generateRandomLengths() { // 0.02 <= currentLength <= 0.05; 0.55 <= maxLength <= 0.66
	//		currentLength = (Math.random() * 4 + 2) / 100;
	//		maxLength = (Math.random() * 12 + 55) / 100;
	//	}
	
	@Override
	public Koi reproduceWith(Koi k) { // returns new Koi with traits selected randomly from parents' traits if reproduction is possible, null otherwise
		//if (currentLength < matureLength || k.currentLength < k.matureLength || sex.equals(k.sex)) // for convenience, method calls will always be of the form male.reproduceWith(female), so the last check should be unnecessary
		if (sex == k.sex)
		{
			return null;
		}
		
		KoiGenome kg = new KoiGenome();
		
		kg.primary = new Coloration(primary.getRandomAllele(), k.primary.getRandomAllele());
		kg.secondary = new Coloration(secondary.getRandomAllele(), k.secondary.getRandomAllele());
		kg.carp = new Gene(carp.getRandomAllele(), k.carp.getRandomAllele());
		kg.carp1 = new Gene(carp1.getRandomAllele(), k.carp1.getRandomAllele());
		kg.blackSpots = new Gene(blackSpots.getRandomAllele(), k.blackSpots.getRandomAllele());
		
		return new Koi(fishWorld, position.cpy(), kg);
	}

	public void printInfo() { // phenotype, not genotype
		System.out.println(this + ":");
		System.out.println("	Secondary color: " + secondary.getExpressedAllele().getName() + ", " + secondary.getExpressedAllele().getColor());
		System.out.println("	Primary color: " + primary.getExpressedAllele().getName() + ", " + primary.getExpressedAllele().getColor());
		System.out.println("	Carp? " + (carp.isExpressed() || carp1.isExpressed()));
		System.out.println("	Black spots? " + blackSpots.isExpressed());
		System.out.println("	Sex: " + sex);
		System.out.println("	Current length: " + currentLength + " m");
		System.out.println("	Mature length: " + matureLength + " m");
		System.out.println("	Max length: " + maxLength + " m");
	}

}