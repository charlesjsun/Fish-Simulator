package com.salty.susan.plants;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.salty.susan.FishWorld;

public class Lily extends Plant
{

	public Lily(FishWorld fishWorld, Vector3 position)
	{
		super(fishWorld, position, 120, true);
	}

	@Override
	public void dispose()
	{
		
	}

	@Override
	protected void loadModel()
	{
		Model m;
		int c = MathUtils.random(0, 2);
		if (c == 0)
		{
			m = fishWorld.getAssetManager().get("models/LilllililiyyyyilyilyliyyllyiyliylyilyiylylllyyyyyiylyilyillFlour.obj");
		}
		else if (c == 1)
		{
			m = fishWorld.getAssetManager().get("models/aPadThatAlsoContainsLilliesAndPadsButThisOneDoesHaveALotus.obj");
		}
		else
		{
			m = fishWorld.getAssetManager().get("models/aPadThatAlsoContainsLilliesAndPadsButThisOneDosentHaveALotus.obj");
		}
		
		modelInstance = new ModelInstance(m);
		
		float r = MathUtils.random(0.0f, 0.25f);
		float g = MathUtils.random(0.75f, 1.0f);
		float b = MathUtils.random(0.0f, 0.25f);
		modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(r, g, b, 1)));
		modelInstance.materials.get(0).set(ColorAttribute.createSpecular(0, 0, 0, 1.0f));
	}
	
}
