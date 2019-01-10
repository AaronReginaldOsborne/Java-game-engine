package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop {
	public static void main(String[] args) {
		
		DisplayManager.createDisplay(); 
		Loader loader = new Loader(); 
		TextMaster.init(loader);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MasterRenderer renderer = new MasterRenderer(loader);
		ParticleMaster.init(loader, renderer.getProjectMatrix());
		
		
		// *********TERRAIN TEXTURE STUFF**********
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("rocky"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("pebbles"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap3"));
		Terrain terrain = new Terrain(0,0,loader,texturePack, blendMap, "elevation");

		// *****************************************
		
		RawModel model = OBJLoader.loadObjModel("pine", loader);
		//objConverter.OBJFileLoader("drone");
		
		TexturedModel tree = new TexturedModel(model,new ModelTexture(loader.loadTexture("pine")));
		
		TexturedModel pokemonTree = new TexturedModel(OBJLoader.loadObjModel("pine", loader), 
				new ModelTexture(loader.loadTexture("pine"))); 
		
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), 
											new ModelTexture(loader.loadTexture("grassTexture"))); 
		ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("ferns"));
		fernAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernAtlas);
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		
		
		
		lamp.getTexture().setUseFakeLighting(true);
		grass.getTexture().setHasTransparency(true); 
		grass.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true); 
		fern.getTexture().setUseFakeLighting(true);
		
		
		
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		Random random = new Random();
		
		Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(0.5f,0.5f,0.5f));
		lights.add(light);
		
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		
		
		float x,y,z;
		for(int i=0;i<440;i++){
			if(i%60 == 0){
				x = random.nextFloat()* 400 - 0;
				z = random.nextFloat() *400- 0;
			 	y = terrain.getHeightOfTerrain(x, z);
				lights.add(new Light(new Vector3f(x,y+14,z ), new Vector3f(1,1,1), new Vector3f(1, 0.01f, 0.002f)));
				entities.add(new Entity(lamp, new Vector3f(x,y,z),0,0,0,1));
			}
			
			if(i%20 == 0){
				x = random.nextFloat()* 400 - 0;
				z = random.nextFloat() *400- 0;
			 	y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(tree, new Vector3f(x,y,z),0,0,0,0.7f));	
				x = random.nextFloat()* 400 - 0;
				z = random.nextFloat() *400- 0;
			 	y = terrain.getHeightOfTerrain(x, z);
				normalMapEntities.add(new Entity(crateModel, new Vector3f(x,y+2,z), 0,0,0,0.025f));
				x = random.nextFloat()* 400 - 0;
				z = random.nextFloat() *400- 0;
			 	y = terrain.getHeightOfTerrain(x, z);
				normalMapEntities.add(new Entity(barrelModel, new Vector3f(x,y+3,z), 0,0,0,0.5f));
			}
			if(i% 3 == 0){
				x = random.nextFloat()*400 - 0;
				z = random.nextFloat() *400;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(pokemonTree, new Vector3f(x,y,z),0,0,0,0.7f));
				x = random.nextFloat()*250 - 0;
				z = random.nextFloat() *400;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(grass, new Vector3f(x,y,z),0,0,0,1.5f));
				x = random.nextFloat()*400 - 0;
				z = random.nextFloat() *400;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(fern,random.nextInt(4), new Vector3f(x,y,z),0,0,0,0.6f));
			}
			
		}
		
		
		

		
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
				loader.loadTexture("playerTexture")));

		Player player = new Player(stanfordBunny, new Vector3f(150, 5,75), 0, 100, 0, 0.375f);
		
		List<GuiTexture> guis =new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("healthBar"),new Vector2f(-0.45f, 0.80f), new Vector2f(0.65f,0.25f));
		guis.add(gui);
		
		
		
		Entity person2 = new Entity(stanfordBunny, new Vector3f(150, 5,75), 0, 100, 0, 0.375f);
		entities.add(player);
		entities.add(person2);
		
		terrains.add(terrain);
		
		Camera camera = new Camera(player);	

		MousePicker picker = new MousePicker(camera, renderer.getProjectMatrix(),terrain);
		
		// *********WATER TEXTURE STUFF**********
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectMatrix(),fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		float waterLevel = 40;
		WaterTile water = new WaterTile(100,100,waterLevel);
		waters.add(water);
		waters.add(new WaterTile(100,300,waterLevel));
		waters.add(new WaterTile(300,100,waterLevel));
		waters.add(new WaterTile(300,300,waterLevel));
		

		
		
		
//		GuiTexture refraction = new GuiTexture(fbos.getRefractionTexture(),new Vector2f(0.5f, 0.5f), new Vector2f(0.25f,0.25f));
//		GuiTexture reflection = new GuiTexture(fbos.getReflectionTexture(),new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f,0.25f));
//		guis.add(refraction);
//		guis.add(reflection);
		
		
		
		// ********************************************
		
		
		// *********TEXT TEXTURE STUFF**********
		
		FontType font = new FontType(loader.loadFont("prototype2"), new File("res/prototype2.fnt"));
		GUIText text = new GUIText("A GOLD FISH", 1.5f ,font , new Vector2f(0.175f,0.08f),0.28f , true);
		text.setWidth(0.5f);
		text.setEdge(0.1f);
		text.setBorderWidth(0.7f);
		text.setBorderEdge(0.1f);
		text.setOutlineColour(1.0f, 0.0f, 0.0f);
		text.setColour(1, 1, 1);
		
		
		// ********************************************
		
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"),4,true);
		ParticleSystem system = new ParticleSystem(particleTexture,50,25,0.3f,4,1f);
		
		
		while(!Display.isCloseRequested()){
			camera.move();
			
			picker.update();
			ParticleMaster.update(camera);
			
			//system.generateParticles(player.getPosition());
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			
			
			
			//System.out.println(picker.getCurrentTerrainPoint());
			if(terrainPoint !=null){
				//person2.setPosition(terrainPoint);
			}
			player.move(terrain);
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.rederScene(entities,normalMapEntities,terrains ,lights, camera, new Vector4f(0,1,0,-water.getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();
			fbos.unbindCurrentFrameBuffer();
			
			fbos.bindRefractionFrameBuffer();
			renderer.rederScene(entities,normalMapEntities,terrains ,lights, camera, new Vector4f(0,-1,0,water.getHeight()));
			fbos.unbindCurrentFrameBuffer();
//			renderer.processEntity(player);
//			renderer.processTerrain(terrain);
//			for(Entity entity:entities){
//				renderer.processEntity(entity);
//			}
//			renderer.render(lights, camera);
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			renderer.rederScene(entities,normalMapEntities,terrains ,lights, camera, new Vector4f(0,1,0,0));
			waterRenderer.render(waters, camera, light);
			
			ParticleMaster.renderParticles(camera);
			
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		fbos.cleanUp();
		DisplayManager.closeDisplay();

	}

}
