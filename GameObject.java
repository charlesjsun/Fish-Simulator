package com.salty.susan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public abstract class GameObject implements Disposable
{
	
	public enum Type
	{
		NONE, KOI, STONE, FISHFOOD, LILY, GENERIC_PLANT;
		
		public Type next()
		{
			return values()[(ordinal() + 1) % values().length];
		}
		
		public Type previous()
		{
			int prev = ordinal() - 1;
			if (prev < 0)
			{
				prev += values().length;
			}
			return values()[prev];
		}
	}
	
	protected ModelInstance modelInstance;
	
	protected FishWorld fishWorld;
	
	protected Vector3 position;
	protected Vector3 velocity;
	protected float rotation; // degrees
	protected float pitch;
	protected int dragForceCoefficient = 10; // F_d = this * v^2; we assume that cross-sectional area is the same for all GameObjects
	protected float scale;
	

	protected float lifeSpan;
	protected boolean markedForDeath = false;
	
	public GameObject(FishWorld fishWorld, Vector3 position, Vector3 velocity, float lifeSpan)
	{
		this.fishWorld = fishWorld;
		this.position = position;
		this.velocity = velocity;
		this.lifeSpan = lifeSpan;
		rotation = 0;
		pitch = 0;
		scale = 1;
		loadModel();
	}
	
	protected abstract void loadModel();
	
	public void update(float dt)
	{
		position.add(velocity.cpy().scl(dt));
		
		lifeSpan -= dt;
		
		if (lifeSpan <= 0)
		{
			markedForDeath = true;
		}
	}
	
	public void render(ModelBatch mb, Environment env)
	{
		modelInstance.transform = new Matrix4(position, new Quaternion().setEulerAngles(rotation, pitch, 0), new Vector3(scale, scale, scale));
		//new Quaternion(new Vector3(0, 1, 0), rotation)
		mb.render(modelInstance, env);
	}
	
	public boolean isMarkedForDeath()
	{
		return markedForDeath;
	}
	
	public Vector3 getPosition()
	{
		return position;
	}
	
	public Vector3 getVelocity()
	{
		return velocity;
	}
	
	public float getDragForce()
	{ // per unit time, this is essentially delta-v
		return dragForceCoefficient * velocity.len() * velocity.len();
	}
	
	public void decreaseVelocityDrag()
	{ // per unit time
		float scale = (velocity.len() - getDragForce()) / velocity.len();
		velocity.scl(scale);
	}
	
}
