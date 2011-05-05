package net.sacredlabyrinth.Phaed.PreciousStones.vectors;

import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.validation.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import net.sacredlabyrinth.Phaed.PreciousStones.AllowedEntry;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import net.sacredlabyrinth.Phaed.PreciousStones.SnitchEntry;
import net.sacredlabyrinth.Phaed.PreciousStones.CloakEntry;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;

/**
 *
 * @author phaed
 */
@Entity()
@CacheStrategy
@Table(name = "fields", uniqueConstraints = @UniqueConstraint(columnNames ={"x", "y", "z", "world"}))
public class Field extends AbstractVec implements Serializable
{
    @Id
    private Long id;
    private int radius;
    private int height;
    private int typeId;

    @NotNull
    private String owner;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "field", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SnitchEntry> snitchList = new ArrayList<SnitchEntry>();

    @OneToMany(mappedBy = "field", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AllowedEntry> allowed = new ArrayList<AllowedEntry>();

    @OneToOne(cascade = CascadeType.ALL)
    private CloakEntry cloakEntry;

    private int chunkX;
    private int chunkZ;

    /**
     *
     */
    public Field()
    {
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param radius
     * @param height
     * @param chunkvec
     * @param world
     * @param typeId
     * @param owner
     * @param allowed
     * @param name
     */
    public Field(int x, int y, int z, int radius, int height, String world, int typeId, String owner, String name)
    {
        super(x, y, z, world);

        this.chunkX = x >> 4;
        this.chunkZ = z >> 4;
        this.radius = radius;
        this.height = height;
        this.owner = owner;
        this.name = name;
        this.typeId = typeId;
    }

    /**
     *
     * @param block
     * @param radius
     * @param height
     * @param owner
     * @param allowed
     * @param name
     */
    public Field(Block block, int radius, int height, String owner, String name)
    {
        super(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());

        this.chunkX = block.getX() >> 4;
        this.chunkZ = block.getZ() >> 4;
        this.radius = radius;
        this.height = height;
        this.owner = owner;
        this.name = name;
        this.typeId = block.getTypeId();
    }

    /**
     *
     * @param block
     * @param radius
     * @param height
     */
    public Field(Block block, int radius, int height)
    {
        super(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());

        this.chunkX = block.getX() >> 4;
        this.chunkZ = block.getZ() >> 4;
        this.radius = radius;
        this.height = height;
        this.typeId = block.getTypeId();
    }

    /**
     *
     * @param block
     */
    public Field(Block block)
    {
        super(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());

        this.chunkX = block.getX() >> 4;
        this.chunkZ = block.getZ() >> 4;
    }

    /**
     *
     * @return
     */
    public Long getId()
    {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the chunk x
     */
    public int getChunkX()
    {
        return chunkX;
    }

    /**
     * @param chunkX
     */
    public void setChunkX(int chunkX)
    {
        this.chunkX = chunkX;
    }

    /**
     * @return the chunk z
     */
    public int getChunkZ()
    {
        return chunkZ;
    }

    /**
     * @param chunkZ
     */
    public void setChunkZ(int chunkZ)
    {
        this.chunkZ = chunkZ;
    }

    /**
     *
     * @param radius
     */
    public void setRadius(int radius)
    {
        this.radius = radius;
        this.setHeight((this.radius * 2) + 1);
    }

    /**
     *
     * @return the block type id
     */
    public int getTypeId()
    {
        return this.typeId;
    }

    /**
     *
     * @return the block type name
     */
    public String getType()
    {
        return Material.getMaterial(this.getTypeId()).toString();
    }

    /**
     *
     * @return the radius
     */
    public int getRadius()
    {
        return this.radius;
    }

    /**
     *
     * @return the height
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     *
     * @return the owner
     */
    public String getOwner()
    {
        return this.owner;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    /**
     *
     * @param playerName
     * @return
     */
    public boolean isOwner(String playerName)
    {
        return playerName.equals(owner);
    }

    /**
     *
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     *
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean isName(String name)
    {
        if (name == null)
        {
            return false;
        }

        return this.name.equalsIgnoreCase(name);
    }

    /**
     * @param allowed the allowed to set
     */
    public void setAllowed(List<AllowedEntry> allowed)
    {
        this.allowed = allowed;
    }

    /**
     *
     * @return
     */
    public List<AllowedEntry> getAllowed()
    {
        return allowed;
    }

    /**
     *
     * @return
     */
    public List<AllowedEntry> getAllAllowed()
    {
        List<AllowedEntry> all = new ArrayList<AllowedEntry>();
        all.add(new AllowedEntry(owner, "all"));
        all.addAll(allowed);
        return all;
    }

    /**
     *
     * @return
     */
    public String getAllowedList()
    {
        String out = "";

        if (allowed.size() > 0)
        {
            for (int i = 0; i < allowed.size(); i++)
            {
                out += ", " + allowed.get(i);
            }
        }
        else
        {
            return null;
        }

        return out.substring(2);
    }

    /**
     *
     * @param allowedName
     * @return
     */
    public boolean isAllowed(String allowedName)
    {
        if (allowedName.equals(owner))
        {
            return true;
        }

        for (AllowedEntry ae : allowed)
        {
            if (ae.getName().equals("*"))
            {
                return true;
            }

            if (ae.getName().equals(allowedName))
            {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param allowedName
     * @return whether the player was allowed
     */
    public boolean addAllowed(String allowedName, String perm)
    {
        if (isAllowed(allowedName))
        {
            return false;
        }

        allowed.add(new AllowedEntry(allowedName, perm));
        return true;
    }

    /**
     *
     * @param allowedName
     * @return whether the player was removed
     */
    public AllowedEntry removeAllowed(String allowedName)
    {
        if (!isAllowed(allowedName))
        {
            return null;
        }

        for (AllowedEntry ae : allowed)
        {
            if (ae.getName().equals(allowedName))
            {
                allowed.remove(ae);
                return ae;
            }
        }

        return null;
    }

    /**
     *
     * @return coordinates string
     */
    public String getCoords()
    {
        return super.toString();
    }

    /**
     *
     * @param name
     * @param reason
     * @param details
     */
    public void addIntruder(String name, String reason, String details)
    {
        for (SnitchEntry se : snitchList)
        {
            if (se.getName().equals(name) && se.getReason().equals(reason) && se.getDetails().equals(details))
            {
                se.addCount();
                return;
            }
        }

        snitchList.add(new SnitchEntry(name, reason, details));
    }

    /**
     * @param snitchList the snitchList to set
     */
    public void setSnitchList(List<SnitchEntry> snitchList)
    {
        this.snitchList = snitchList;
    }

    /**
     *
     * @return
     */
    public List<SnitchEntry> getSnitchList()
    {
        return snitchList;
    }

    /**
     *
     */
    public void cleanSnitchList()
    {
        snitchList.clear();
    }

    /**
     *
     * @return the cloak entry
     */
    public CloakEntry getCloakEntry()
    {
        return cloakEntry;
    }

    /**
     *
     * @param cloakEntry
     */
    public void setCloakEntry(CloakEntry cloakEntry)
    {
        this.cloakEntry = cloakEntry;
    }

    @Override
    public String toString()
    {
        return super.toString() + " [" + getOwner() + "]";
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
    }

    /**
     * @return the chunkvec
     */
    public ChunkVec getChunkVec()
    {
        return new ChunkVec(getX() >> 4, getZ() >> 4, getWorld());
    }

    /**
     *
     * @return vectors of the corners
     */
    public ArrayList<Vector> getCorners()
    {
        ArrayList<Vector> corners = new ArrayList<Vector>();

        int minx = getX() - getRadius();
        int maxx = getX() + getRadius();
        int minz = getZ() - getRadius();
        int maxz = getZ() + getRadius();
        int miny = getY() - (int) Math.floor(((double) getHeight()) / 2);
        int maxy = getY() + (int) Math.ceil(((double) getHeight()) / 2);

        corners.add(new Vector(minx, miny, minz));
        corners.add(new Vector(minx, miny, maxz));
        corners.add(new Vector(minx, maxy, minz));
        corners.add(new Vector(minx, maxy, maxz));
        corners.add(new Vector(maxx, miny, minz));
        corners.add(new Vector(maxx, miny, maxz));
        corners.add(new Vector(maxx, maxy, minz));
        corners.add(new Vector(maxx, maxy, maxz));

        return corners;
    }

    /**
     *
     * @param field
     * @return whether the fields intersect
     */
    public boolean intersects(Field field)
    {
        if (!field.getWorld().equals(getWorld()))
        {
            return false;
        }

        ArrayList<Vector> corners = field.getCorners();

        for (Vector vec : corners)
        {
            if (this.envelops(vec))
            {
                return true;
            }
        }

        corners = this.getCorners();

        for (Vector vec : corners)
        {
            if (field.envelops(vec))
            {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param vec
     * @return whether vector is enveloped by the field
     */
    public boolean envelops(Vector vec)
    {
        int px = vec.getBlockX();
        int py = vec.getBlockY();
        int pz = vec.getBlockZ();

        int minx = getX() - getRadius();
        int maxx = getX() + getRadius();
        int minz = getZ() - getRadius();
        int maxz = getZ() + getRadius();
        int miny = getY() - (int) Math.floor(((double) getHeight()) / 2);
        int maxy = getY() + (int) Math.ceil(((double) getHeight()) / 2);

        if (px >= minx && px <= maxx && py >= miny && py <= maxy && pz >= minz && pz <= maxz)
        {
            return true;
        }

        return false;
    }

    /**
     *
     * @param field
     * @return whether field block is enveloped by the field
     */
    public boolean envelops(AbstractVec field)
    {
        int px = field.getX();
        int py = field.getY();
        int pz = field.getZ();

        int minx = getX() - getRadius();
        int maxx = getX() + getRadius();
        int minz = getZ() - getRadius();
        int maxz = getZ() + getRadius();
        int miny = getY() - (int) Math.floor(((double) getHeight()) / 2);
        int maxy = getY() + (int) Math.ceil(((double) getHeight()) / 2);

        if (px >= minx && px <= maxx && py >= miny && py <= maxy && pz >= minz && pz <= maxz)
        {
            return true;
        }

        return false;
    }

    /**
     *
     * @param block
     * @return whether block is enveloped by the field
     */
    public boolean envelops(Block block)
    {
        return envelops(new Vec(block));
    }
}
