package com.salty.susan.animals;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.salty.susan.FishWorld;
import com.salty.susan.GameObject;

public abstract class Animal<T extends Animal<T>> extends GameObject
{
	
	public enum Sex
	{
		MALE, FEMALE
	}
	
	protected Class<T> type;
	
	protected Sex sex;
	protected float currentLength, matureLength, maxLength; // in meters
			
	protected Vector3 target;
	
	protected float maxTimeToReproduction;
	protected float timeToReproduction;
	protected boolean wantsToReproduce;
	protected T toReproduceWith;
	
	public Animal(FishWorld fishWorld, Vector3 position, Class<T> type)
	{
		super(fishWorld, position, new Vector3(), 40);
		this.fishWorld = fishWorld;
		this.type = type;
		target = position.cpy();
		
		maxTimeToReproduction = 10.0f;
		timeToReproduction = maxTimeToReproduction;
		wantsToReproduce = false;
		toReproduceWith = null;
	}
	
	@Override
	public void update(float dt)
	{
		super.update(dt);
		
		if (wantsToReproduce && toReproduceWith != null)
		{
			velocity = toReproduceWith.position.cpy().sub(position).nor().scl(8);
			
			if (position.dst2(toReproduceWith.position) < 1)
			{
				if (sex == Sex.MALE)
				{
					T child = reproduceWith(toReproduceWith);
					fishWorld.spawnGameObject(child);
				
					toReproduceWith.wantsToReproduce = false;
					toReproduceWith.toReproduceWith = null;
					toReproduceWith.timeToReproduction = toReproduceWith.maxTimeToReproduction;
					
					toReproduceWith.recalculateTarget();
					
					wantsToReproduce = false;
					toReproduceWith = null;
					timeToReproduction = maxTimeToReproduction;
					
					recalculateTarget();
				
				}
			}
		}
		else
		{
			if (position.dst2(target) < 1)
			{
				recalculateTarget();
			}
		}
		
		timeToReproduction -= dt;
		if (timeToReproduction < 0 && toReproduceWith == null)
		{
			wantsToReproduce = true;
			ArrayList<T> others = fishWorld.getObjectsOfType(type);
			while (others.size() > 0)
			{
				int index = MathUtils.random(0, others.size() - 1);
				T other = others.get(index);
				if (other.wantsToReproduce && other.sex != sex && other.toReproduceWith == null)
				{
					toReproduceWith = other;
					other.toReproduceWith = (T) this;
					break;
				}
				others.remove(index);
			}
		}
		
		
		
		rotation = (float) (90.0f - Math.toDegrees(Math.atan2(velocity.z, velocity.x)));
		pitch = (float) (-Math.toDegrees(Math.atan(velocity.y / Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z))));
		
	}
	
	public void recalculateTarget()
	{
		do
		{
			float width = fishWorld.getTerrain().getWidth();
			float radius = width / 8;
			float x = MathUtils.random(Math.max(0, position.x - radius), Math.min(width, position.x + radius));
			float z = MathUtils.random(Math.max(0, position.z - radius), Math.min(width, position.z + radius));
			float y = MathUtils.random(fishWorld.getTerrain().getHeight(x, z), fishWorld.getTerrain().getWaterLevel());
			target.set(x, y, z);
		} while (!fishWorld.getTerrain().isInBound(target));
		
		velocity = target.cpy().sub(position).nor().scl(MathUtils.random(3, 10));
	}
	
	public abstract T reproduceWith(T a);
	
	public static Color generateRandomColor(int rMin, int rMax, int gMin, int gMax, int bMin, int bMax)
	{ // returns random Color within the specified bounds, inclusive
		int r = (int) (Math.random() * (rMax - rMin + 1) + rMin);
		int g = (int) (Math.random() * (gMax - gMin + 1) + gMin);
		int b = (int) (Math.random() * (bMax - bMin + 1) + bMin);
		return new Color(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
	}
	
	public float generateRandomLength(float min, float max)
	{
		if (min == max)
			return min;
		if (min > max)
			return generateRandomLength(max, min);
		return MathUtils.random(min, max);
	}
	
	public void generateRandomSex()
	{ // really only for animals
		if (Math.random() < 0.5) sex = Sex.MALE;
		else sex = Sex.FEMALE;
	}
}
