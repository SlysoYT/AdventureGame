package game.level.generation;

import java.util.Random;

import game.level.tile.Tile;

public class GenerateLevel
{
	private static final int size = 512; //Chunk size

	public static int[] generateLevel(long seed)
	{
		Random rand = new Random(seed);
		float[] normValues = generateLevelNormValues(seed);
		int[] tileValues = new int[size * size];

		//Convert norm values to tile values containing corresponding rgb values
		for(int i = 0; i < normValues.length; i++)
		{
			//Grass biome
			if(normValues[i] < 0.5F)
			{
				if(normValues[i] < 0.27F) tileValues[i] = Tile.COL_TILE_DIRT;
				else if(normValues[i] < 0.32F) tileValues[i] = Tile.COL_TILE_WATER;
				else
				{
					int random = rand.nextInt() % 70;
					if(random == 0) tileValues[i] = Tile.COL_TILE_FLOWER_0;
					else if(random == 1) tileValues[i] = Tile.COL_TILE_FLOWER_1;
					else if(random == 2) tileValues[i] = Tile.COL_TILE_FLOWER_2;
					else if(random == 3) tileValues[i] = Tile.COL_TILE_FLOWER_3;
					else if(random == 4) tileValues[i] = Tile.COL_TILE_ROCK_GRASS;
					else tileValues[i] = Tile.COL_TILE_GRASS;
				}
			}
			//Sand biome
			else
			{
				if(normValues[i] < 0.1F) tileValues[i] = Tile.COL_TILE_WATER;
				else
				{
					int random = rand.nextInt() % 70;
					if(random == 0) tileValues[i] = Tile.COL_TILE_ROCK_SAND;
					else tileValues[i] = Tile.COL_TILE_SAND;
				}
			}
		}

		return tileValues;
	}

	private static float[] generateLevelNormValues(long seed)
	{
		int stepSize = size;
		Random rand = new Random(seed);
		float[][] tiles = { new float[size * size], new float[size * size], new float[size * size], new float[size * size], new float[size * size] };
		int[] weights = { 20, 5, 4, 3, 2 };

		for(int i = 0; i < 5; i++)
		{
			//Random values with distance as step size
			for(int x = 0; x < size; x += stepSize + 1 * ((x % 2) - 1))
			{
				for(int y = 0; y < size; y += stepSize + 1 * ((y % 2) - 1))
				{
					tiles[i][x + y * size] = (float) (rand.nextFloat() * weights[i]);
				}
			}
			//Interpolate chunk given the random values
			for(int x = 0; x < size / stepSize; x++)
			{
				for(int y = 0; y < size / stepSize; y++)
				{
					linearInterpolation(tiles[i], x * stepSize - 1 * x % 2, y * stepSize - 1 * y % 2, (x + 1) * stepSize - 1, (y + 1) * stepSize - 1);
				}
			}
			stepSize *= 0.5;
		}

		int maxValue = 0;
		for(int i = 0; i < weights.length; i++)
			maxValue += weights[i];
		return normArrayValues(getSumOfArrayValues(tiles), maxValue);
	}

	private static float[] linearInterpolation(float[] tiles, int xStart, int yStart, int xBound, int yBound)
	{
		int sizeTiles = (int) Math.sqrt(tiles.length);
		int width = xBound - xStart;
		int height = yBound - yStart;

		//Convert to 2D array
		float[][] tile = new float[width + 1][height + 1];
		for(int y = yStart; y <= yBound; y++)
		{
			for(int x = xStart; x <= xBound; x++)
			{
				tile[x - xStart][y - yStart] = tiles[x + y * sizeTiles];
			}
		}

		//Line with y = yStart
		float a = tile[0][0];
		float b = tile[width][0];

		for(int x = 1; x < width; x++)
		{
			float normVal = (float) x / width;
			tile[x][0] = a + normVal * (b - a);
		}

		//Line with y = yBound
		a = tile[0][height];
		b = tile[width][height];

		for(int x = 1; x < width; x++)
		{
			float normVal = (float) x / width;
			tile[x][height] = a + normVal * (b - a);
		}

		//Lines with x = xStart - x = xBound
		for(int x = 0; x <= width; x++)
		{
			a = tile[x][0];
			b = tile[x][height];
			for(int y = 1; y < height; y++)
			{
				float normVal = (float) y / width;
				tile[x][y] = a + normVal * (b - a);
			}
		}

		//Put 2D array back into 1D array
		for(int y = yStart; y <= yBound; y++)
		{
			for(int x = xStart; x <= xBound; x++)
			{
				tiles[x + y * sizeTiles] = tile[x - xStart][y - yStart];
			}
		}

		return tiles;
	}

	private static float[] getSumOfArrayValues(float[][] array)
	{
		float[] sum = array[0].clone();
		for(int i = 1; i < array.length; i++)
		{
			for(int j = 0; j < array[i].length; j++)
			{
				sum[j] += array[i][j];
			}
		}
		return sum;
	}

	private static float[] normArrayValues(float[] array, float maxValue)
	{
		for(int i = 0; i < array.length; i++)
		{
			array[i] /= maxValue;
		}
		return array;
	}

	public static void printGeneratedLevel(float[] tiles)
	{
		int size = (int) Math.sqrt(tiles.length);

		for(int y = 0; y < size; y++)
		{
			for(int x = 0; x < size; x++)
			{
				System.out.print(tiles[x + y * size] + "  ");
			}
			System.out.println("");
		}
	}
}
