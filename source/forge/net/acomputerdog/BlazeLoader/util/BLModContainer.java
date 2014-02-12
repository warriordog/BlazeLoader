package net.acomputerdog.BlazeLoader.util;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;
import net.acomputerdog.BlazeLoader.main.Version;

import java.io.File;
import java.security.cert.Certificate;
import java.util.*;

public class BLModContainer implements ModContainer {
    public static final BLModContainer instance = new BLModContainer();

    private BLModContainer() {
    }

    private File source = identifySource();
    private ModMetadata metadata = identifyMetadata();
    private ArtifactVersion artifactVersion = identifyArtifactVersion();
    private VersionRange versionRange = identifyVersionRange();

    private File identifySource() {
        try {
            return new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            return null;
        }
    }

    private ModMetadata identifyMetadata() {
        ModMetadata meta = new ModMetadata();
        meta.version = Version.getStringVersion();
        meta.authorList.add("acomputerdog");
        meta.credits = "Big thanks to mumfrey, BigXplosion, and all who contribute on github!";
        meta.modId = getModId();
        meta.name = getName();
        meta.description = "The BlazeLoader mod API.  BlazeLoader allows the creation of advanced and game-changing mods with minimal code changes.  It also provides forge compatibility for other mods, saving developers the trouble of porting their mods.";
        meta.url = "http://www.minecraftforum.net/topic/2007563-172-blazeloader-mod-loader-and-api-for-game-changing-mods/";
        meta.logoFile = "/BlazeLoader/res/BLLogo.png";
        return meta;
    }

    private ArtifactVersion identifyArtifactVersion() {
        return new ArtifactVersion() {
            @Override
            public String getLabel() {
                return "BlazeLoader";
            }

            @Override
            public String getVersionString() {
                return Version.getStringVersion();
            }

            @Override
            public boolean containsVersion(ArtifactVersion source) {
                return compareTo(source) >= 0;
            }

            @Override
            public String getRangeString() {
                return "";
            }

            @Override
            public int compareTo(ArtifactVersion o) {
                String[] strVersionsThis = getVersionString().split(".");
                String[] strVersionsThat = o.getVersionString().split(".");
                if (strVersionsThis.length != strVersionsThat.length) {
                    return 0;
                }
                for (int index = 0; index < strVersionsThis.length; index++) {
                    try {
                        int verThis = Integer.parseInt(strVersionsThis[index]);
                        int verThat = Integer.parseInt(strVersionsThat[index]);
                        if (verThis > verThat) {
                            return 1;
                        }
                        if (verThis < verThat) {
                            return -1;
                        }
                    } catch (NumberFormatException ignored) {
                        return 0;
                    }
                }
                return 0;
            }
        };
    }

    private VersionRange identifyVersionRange() {
        return VersionRange.createFromVersion(Version.getStringVersion(), artifactVersion);
    }

    /**
     * The globally unique modid for this mod
     */
    @Override
    public String getModId() {
        return "BlazeLoader";
    }

    /**
     * A human readable name
     */
    @Override
    public String getName() {
        return "BlazeLoader";
    }

    /**
     * A human readable version identifier
     */
    @Override
    public String getVersion() {
        return Version.getStringVersion();
    }

    /**
     * The location on the file system which this mod came from
     */
    @Override
    public File getSource() {
        return source;
    }

    /**
     * The metadata for this mod
     */
    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    /**
     * Attach this mod to it's metadata from the supplied metadata collection
     *
     * @param mc
     */
    @Override
    public void bindMetadata(MetadataCollection mc) {

    }

    /**
     * Set the enabled/disabled state of this mod
     *
     * @param enabled
     */
    @Override
    public void setEnabledState(boolean enabled) {
        //You can't stop me!
    }

    /**
     * A list of the modids that this mod requires loaded prior to loading
     */
    @Override
    public Set<ArtifactVersion> getRequirements() {
        return new HashSet<ArtifactVersion>();
    }

    /**
     * A list of modids that should be loaded prior to this one. The special
     * value <strong>*</strong> indicates to load <em>after</em> any other mod.
     */
    @Override
    public List<ArtifactVersion> getDependencies() {
        return new ArrayList<ArtifactVersion>();
    }

    /**
     * A list of modids that should be loaded <em>after</em> this one. The
     * special value <strong>*</strong> indicates to load <em>before</em> any
     * other mod.
     */
    @Override
    public List<ArtifactVersion> getDependants() {
        return new ArrayList<ArtifactVersion>();
    }

    /**
     * A representative string encapsulating the sorting preferences for this
     * mod
     */
    @Override
    public String getSortingRules() {
        return null;
    }

    /**
     * Register the event bus for the mod and the controller for error handling
     * Returns if this bus was successfully registered - disabled mods and other
     * mods that don't need real events should return false and avoid further
     * processing
     *
     * @param bus
     * @param controller
     */
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return false;
    }

    /**
     * Does this mod match the supplied mod
     *
     * @param mod
     */
    @Override
    public boolean matches(Object mod) {
        if (!(mod instanceof ModContainer)) return false;
        ModContainer container = (ModContainer) mod;
        return (getModId().equals(container.getModId()) && getVersion().equals(container.getVersion()));
    }

    /**
     * Get the actual mod object
     */
    @Override
    public Object getMod() {
        return null;
    }

    @Override
    public ArtifactVersion getProcessedVersion() {
        return artifactVersion;
    }

    @Override
    public boolean isImmutable() {
        return true;
    }

    @Override
    public String getDisplayVersion() {
        return Version.getStringVersion();
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return versionRange;
    }

    @Override
    public Certificate getSigningCertificate() {
        return null;
    }

    @Override
    public Map<String, String> getCustomModProperties() {
        return new HashMap<String, String>();
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        return null;
    }

    @Override
    public Map<String, String> getSharedModDescriptor() {
        return new HashMap<String, String>();
    }

    @Override
    public Disableable canBeDisabled() {
        return Disableable.NEVER;
    }

    @Override
    public String getGuiClassName() {
        return null;
    }

    @Override
    public List<String> getOwnedPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("net.acomputerdog.Blazeloader");
        return packages;
    }
}
