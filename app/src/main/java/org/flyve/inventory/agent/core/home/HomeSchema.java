package org.flyve.inventory.agent.core.home;

/*
 *   Copyright © 2018 Teclib. All rights reserved.
 *
 *   This file is part of flyve-mdm-android
 *
 * flyve-mdm-android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @date      18/2/18
 * @copyright Copyright © 2018 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */
public class HomeSchema {

    private String id;
    private String title;
    private String subTitle;
    private Boolean hasCheck;
    private Boolean checkValue;
    private Boolean isHeader;

    public HomeSchema(String id, String title) {
        this.id = id;
        this.title = title;
        this.hasCheck = false;
        this.isHeader = true;
    }

    public HomeSchema(String id, String title, String subTitle) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.hasCheck = false;
        this.isHeader = false;
    }

    public HomeSchema(String id, String title, String subTitle, Boolean checkValue) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.hasCheck = true;
        this.isHeader = false;
        this.checkValue = checkValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Boolean getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(Boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public Boolean getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(Boolean checkValue) {
        this.checkValue = checkValue;
    }

    public Boolean getHeader() {
        return isHeader;
    }

    public void setHeader(Boolean header) {
        isHeader = header;
    }
}
