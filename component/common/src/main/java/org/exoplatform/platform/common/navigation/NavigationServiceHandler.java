/**
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.common.navigation;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.core.BaseWebSchemaHandler;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 26/11/12
 */
public class NavigationServiceHandler extends BaseWebSchemaHandler {

    private static Log logger = ExoLogger.getLogger(NavigationServiceHandler.class);
    Calendar calendar = new GregorianCalendar();

    public void onCreateNode(SessionProvider sessionProvider, final Node portalFolder) throws Exception {

        if (portalFolder.hasNode("ApplicationData")) {
            Node applicationDataFolder = portalFolder.getNode("ApplicationData");
            Node logoApplicationFolder = applicationDataFolder.addNode("logo", NT_UNSTRUCTURED);
            addMixin(logoApplicationFolder, "exo:owneable");
            addMixin(logoApplicationFolder, "exo:datetime");
            addMixin(logoApplicationFolder, "exo:hiddenable");
            logoApplicationFolder.setProperty("exo:dateCreated", calendar);

        }

        portalFolder.getSession().save();
    }

    public static String getHomePageLogoURI() {
        Boolean isavailable = false;
        Node imageNode = null;
        String pathImageNode = null;
        try {
            NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
            SessionProvider sProvider = SessionProvider.createSystemProvider();
            Node publicApplicationNode = nodeCreator.getPublicApplicationNode(sProvider);
            Session session = publicApplicationNode.getSession();
            Node rootNode = session.getRootNode();
            String portalName = Util.getPortalRequestContext().getPortalOwner();
            String path = "sites content/live/" + portalName + "/ApplicationData/logo/";
            Node logoNode = rootNode.getNode(path);
            if (logoNode.hasNodes()) {
                for (NodeIterator iterator = logoNode.getNodes(); iterator.hasNext(); ) {
                    Node node = iterator.nextNode();
                    imageNode = node;
                    if (imageNode.hasNode("jcr:content")) {
                        if (imageNode.getNode("jcr:content").hasProperty("jcr:mimeType")) {
                            String JcrMimeType = imageNode.getNode("jcr:content").getProperty("jcr:mimeType").getString();
                            if (JcrMimeType.equals("image/gif") || JcrMimeType.equals("image/ief") ||
                                    JcrMimeType.equals("image/jpeg") || JcrMimeType.equals("image/pjpeg") ||
                                    JcrMimeType.equals("image/bmp") || JcrMimeType.equals("image/x-portable-bitmap") ||
                                    JcrMimeType.equals("image/x-portable-graymap") || JcrMimeType.equals("image/png") ||
                                    JcrMimeType.equals("image/x-png") || JcrMimeType.equals("image/x-portable-anymap") ||
                                    JcrMimeType.equals("image/x-portable-pixmap") || JcrMimeType.equals("image/x-cmu-raster") ||
                                    JcrMimeType.equals("image/x-rgb") || JcrMimeType.equals("image/tiff") ||
                                    JcrMimeType.equals("image/x-xbitmap") || JcrMimeType.equals("image/x-xpixmap") ||
                                    JcrMimeType.equals("image/x-xwindowdump")) {

                                isavailable = true;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Get logo : Can not Find node", e);
        }
        if (isavailable == true) {
            try {
                pathImageNode = imageNode.getPath();
            } catch (RepositoryException e) {
                logger.error("Get logo : Can not Find the path of node", e);
            }
            return pathImageNode;
        } else
            return null;
    }


    @Override
    protected String getHandlerNodeType() {
        return "exo:portalFolder";
    }

    @Override
    protected String getParentNodeType() {
        return "nt:unstructured";
    }
}
