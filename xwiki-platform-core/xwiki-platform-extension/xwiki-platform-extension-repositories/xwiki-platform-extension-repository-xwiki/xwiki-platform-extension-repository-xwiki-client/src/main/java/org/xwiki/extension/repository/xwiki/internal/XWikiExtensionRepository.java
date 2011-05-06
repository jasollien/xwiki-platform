/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.extension.repository.xwiki.internal;

import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.restlet.data.MediaType;
import org.xwiki.extension.Extension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.ResolveException;
import org.xwiki.extension.repository.AbstractExtensionRepository;
import org.xwiki.extension.repository.ExtensionRepositoryId;
import org.xwiki.extension.repository.Searchable;

/**
 * 
 * @version $Id$
 */
public class XWikiExtensionRepository extends AbstractExtensionRepository implements Searchable
{
    private XWikiExtensionRepositoryFactory repositoryFactory;

    private final UriBuilder extensionUriBuider;

    public XWikiExtensionRepository(ExtensionRepositoryId repositoryId,
        XWikiExtensionRepositoryFactory repositoryFactory) throws Exception
    {
        super(repositoryId);

        this.repositoryFactory = repositoryFactory;

        // Uri builders
        this.extensionUriBuider = createUriBuilder("/extension/{extensionId}/{extensionVersion}");
    }

    // ExtensionRepository

    public Extension resolve(ExtensionId extensionId) throws ResolveException
    {
        String url;
        try {
            url = this.extensionUriBuider.build(extensionId.getId(), extensionId.getVersion()).toString();
        } catch (Exception e) {
            throw new ResolveException("Failed to build REST URL for extension [" + extensionId + "]", e);
        }

        HttpClient httpClient = createClient();

        GetMethod getMethod = new GetMethod(url.toString());
        getMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
        try {
            httpClient.executeMethod(getMethod);
        } catch (Exception e) {
            throw new ResolveException("Failed to request extension [" + extensionId + "]", e);
        }

        if (getMethod.getStatusCode() != HttpStatus.SC_OK) {
            throw new ResolveException("Invalid answer fo the server when requesting extension [" + extensionId + "]");
        }

        try {
            return (XWikiExtension) this.repositoryFactory.getUnmarshaller().unmarshal(
                getMethod.getResponseBodyAsStream());
        } catch (Exception e) {
            throw new ResolveException("Failed to create extension object for extension [" + extensionId + "]", e);
        }
    }

    private HttpClient createClient()
    {
        HttpClient httpClient = new HttpClient();

        return httpClient;
    }

    private UriBuilder createUriBuilder(String path)
    {
        return UriBuilder.fromUri(getId().getURI()).path(path);
    }

    public boolean exists(ExtensionId extensionId)
    {
        // TODO
        return false;
    }

    // Searchable

    public List<Extension> search(String query)
    {
        // TODO Auto-generated method stub
        return null;
    }
}