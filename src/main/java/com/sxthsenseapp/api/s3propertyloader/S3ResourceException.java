package com.sxthsenseapp.api.s3propertyloader;

/*
 * #%L
 * S3Property Loader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 SixthSenseApp
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

public class S3ResourceException extends RuntimeException {

    public S3ResourceException( String msg ) {
        super( msg );
    }
    public S3ResourceException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}
