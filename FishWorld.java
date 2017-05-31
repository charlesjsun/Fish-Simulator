package com.salty.susan;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.salty.susan.animals.Koi;
import com.salty.susan.plants.GenericPlant;
import com.salty.susan.plants.Lily;
import com.salty.susan.things.FishFood;
import com.salty.susan.things.Rock;

public class FishWorld implements Disposable
{
	
	private Environment environment;
	private PerspectiveCamera camera;
	private FirstPersonCameraController camController;
	
	private Model test;
	private ModelInstance testInstance;
	
	private AssetManager assetManager;
	
	private Terrain terrain;
	
	private float camVelocity = 20f;
	
	public static boolean isWireFrame = false;
	
	// For one click only
	private boolean isClicking = false;
	
	private ArrayList<GameObject> gameObjects;

	private GameObject.Type currentSelected = GameObject.Type.NONE;
	
	private float elapsedTime;
	
	public FishWorld(PerspectiveCamera cam)
	{
		this.camera = cam;
		
		camController = new FirstPersonCameraController(cam);
		camController.setVelocity(camVelocity);
		Gdx.input.setInputProcessor(camController);
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.0f));
		environment.add(new DirectionalLight().set(new Color(1.0f, 1.0f, 1.0f, 1.0f), new Vector3(-1.0f, -0.8f, -0.2f)));
		
		assetManager = new AssetManager();
		loadAssets();
		
		ModelBuilder mb = new ModelBuilder();
		test = mb.createBox(4, 4, 4, new Material(ColorAttribute.createDiffuse(Color.YELLOW)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		testInstance = new ModelInstance(test);
		
		terrain = new Terrain(60, 5, 40, cam);
		
		gameObjects = new ArrayList<GameObject>();
		
		elapsedTime = 0f;
		
		spawnThings();
	}
	
	private void loadAssets()
	{
		assetManager.load("models/koi.obj", Model.class);
		assetManager.load("models/pelletForWhichToFeedFish.obj", Model.class);
		assetManager.load("models/rockMyWorld.obj", Model.class);
		assetManager.load("models/rockAlsoButDifferentThanOtherRocksInItsOwnSpecialWay.obj", Model.class);
		assetManager.load("models/freshMotherHeckingRockLikeProbablyTheFreshestIveEverSeen.obj", Model.class);
		assetManager.load("models/toRockAroundToRockAroundYouGetTricky.obj", Model.class);
		assetManager.load("models/LilllililiyyyyilyilyliyyllyiyliylyilyiylylllyyyyyiylyilyillFlour.obj", Model.class);
		assetManager.load("models/aPadThatAlsoContainsLilliesAndPadsButThisOneDoesHaveALotus.obj", Model.class);
		assetManager.load("models/aPadThatAlsoContainsLilliesAndPadsButThisOneDosentHaveALotus.obj", Model.class);
		assetManager.load("models/allKindsOfGenericPlant.obj", Model.class);
		
		assetManager.finishLoading();
	}
	
	private void spawnThings()
	{
		for (int i = 0; i < 0; i++)
		{
			spawnKoi();
		}
	}
	
	private void spawnKoi()
	{
		Vector3 position = getAnimalSpawnLoc();
		gameObjects.add(new Koi(this, position));
	}
	
	public void spawnGameObject(Vector3 position)
	{
		
		switch (currentSelected)
		{
		case NONE: // NONE Selected
			break; 
		case KOI: // Koi
			gameObjects.add(new Koi(this, position.cpy()));
			break;
		case STONE: // Stone
			gameObjects.add(new Rock(this, position.cpy()));
			break;
		case FISHFOOD: // FishFood
			gameObjects.add(new FishFood(this, position.cpy()));
			break;
		case LILY:
			gameObjects.add(new Lily(this, position.cpy()));
			break;
		case GENERIC_PLANT:
			gameObjects.add(new GenericPlant(this, position.cpy()));
			break;
		}
	}
	
	public void spawnGameObject(GameObject go)
	{
		gameObjects.add(go);
	}
	
	@Override
	public void dispose()
	{
		test.dispose();
		terrain.dispose();
		
		for (GameObject go : gameObjects)
		{
			go.dispose();
		}
		
		assetManager.dispose();
		
		gameObjects.clear();
		
	}
	
	public void update(float dt)
	{
		
		// Global Timer
		elapsedTime += dt;
		if (elapsedTime > 1e31f)
		{
			elapsedTime -= 1e31f;
		}
		
		// Call update method of all Objects
		camController.update();
		terrain.update(dt);
		for (int i = 0; i < gameObjects.size(); i++)
		{
			gameObjects.get(i).update(dt);
		}
		for (int i = gameObjects.size() - 1; i >= 0; i--)
		{
			if (gameObjects.get(i).isMarkedForDeath())
				gameObjects.remove(i).dispose();
		}
		
		// Speeds up camera - Sprinting
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
		{
			camController.setVelocity(camVelocity * 5);
		}
		else
		{
			camController.setVelocity(camVelocity);
		}
		
		// Wireframe mode
		if (Gdx.input.isKeyJustPressed(Input.Keys.T))
		{
			isWireFrame = !isWireFrame;
		}
		
		// Iterating through all GameObject types
		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
		{
			currentSelected = currentSelected.next();
			System.out.println(currentSelected);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
		{
			currentSelected = currentSelected.previous();
			System.out.println(currentSelected);
		}
		
		// Left clicking, Rising Edge
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
		{
			//if (!isClicking)
			{
				isClicking = true;
				Vector3 spawn = getMouseClick();
				if (spawn != null && terrain.isInBound(spawn))
				{
					spawnGameObject(spawn);
				}
			}
		}
		else
		{
			isClicking = false;
		}
		
	}
	
	public void render(ModelBatch mb)
	{
		mb.render(terrain, environment);
		
		mb.render(testInstance, environment);
		
		for (GameObject go : gameObjects)
		{
			go.render(mb, environment);
		}
	}


	public BitmapFont font = new BitmapFont();;
	
	public void renderGUI(SpriteBatch gui)
	{
		font.setColor(new Color(0, 0, 0, 1));
		font.draw(gui, "#fps: " + Gdx.graphics.getFramesPerSecond() + " #Currently Selected: " + currentSelected + " #Time Scale: " + FishSimulator.timeScale , 0, 20);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends GameObject> ArrayList<T> getObjectsOfType(Class<T> type)
	{
		ArrayList<T> ret = new ArrayList<T>();
		for (GameObject go : gameObjects)
		{
			if (type.isInstance(go))
			{
				ret.add((T)go);
			}
		}
		return ret;
	}
	
	public Vector3 getAnimalSpawnLoc()
	{
		Vector3 position = new Vector3();
		do
		{
			float x = MathUtils.random(0, terrain.getWidth());
			float z = MathUtils.random(0, terrain.getWidth());
			float y = MathUtils.random(terrain.getHeight(x, z), terrain.getWaterLevel());
			position.set(x, y, z);
		} while (!terrain.isInBound(position));
		
		return position;
	}
	
	public Vector3 getPlantSpawnLoc(boolean isFloating)
	{
		Vector3 position = new Vector3();
		do
		{
			float x = MathUtils.random(0, terrain.getWidth());
			float z = MathUtils.random(0, terrain.getWidth());
			float y = MathUtils.random(terrain.getHeight(x, z), terrain.getWaterLevel());
			position.set(x, y, z);
		} while (!terrain.isInBound(position));
		
		if (isFloating)
		{
			position.y = terrain.getWaterLevel();
		}
		else
		{
			position.y = terrain.getHeight(position.x, position.z);
		}
		return position;
		
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public float getElapsedTime()
	{
		return elapsedTime;
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	public Terrain getTerrain()
	{
		return terrain;
	}
	
	public Vector3 getMouseClick()
	{
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
		{
			Vector3 forward = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).direction.cpy().nor();
			
			if (forward.y != 0)
			{
				float height = camera.position.y - terrain.getWaterLevel();
				float sc = height / Math.abs(forward.y);
				
				forward.scl(sc);
				
				return camera.position.cpy().add(forward);
			}
		}
		return null;
	}
	
}
