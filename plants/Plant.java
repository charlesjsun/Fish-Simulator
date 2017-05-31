package com.salty.susan.plants;

import com.badlogic.gdx.math.Vector3;
import com.salty.susan.FishWorld;
import com.salty.susan.GameObject;

public abstract class Plant extends GameObject
{

	protected boolean isFloating;
	
	public Plant(FishWorld fishWorld, Vector3 position, float lifeSpan, boolean isFloating)
	{
		super(fishWorld, position, new Vector3(), lifeSpan);
		this.isFloating = isFloating;
		if (!isFloating)
		{
			velocity.y = -5.0f;
		}
	}
	
	@Override
	public void update(float dt)
	{
		super.update(dt);
		
		if (isFloating)
		{
			position.y = fishWorld.getTerrain().getDynamicWaterLevel(position.x, position.z);
		}
		else
		{
			if (position.y <= fishWorld.getTerrain().getHeight(position.x, position.z))
			{
				velocity.y = 0;
			}
		}
	}

}
