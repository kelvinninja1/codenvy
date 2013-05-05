/*
 *    Copyright (C) 2013 Codenvy.
 *
 */
package com.codenvy.analytics.scripts;


import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.codenvy.analytics.metrics.MetricParameter;
import com.codenvy.analytics.metrics.value.ListListStringValueData;
import com.codenvy.analytics.metrics.value.LongValueData;
import com.codenvy.analytics.metrics.value.MapStringLongValueData;
import com.codenvy.analytics.metrics.value.SetListStringValueData;
import com.codenvy.analytics.metrics.value.SetStringValueData;
import com.codenvy.analytics.metrics.value.ValueData;

/**
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public enum ScriptType {

    EVENT_COUNT_USERS_CREATED_PROJECTS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_WORKSPACE_CREATED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_USER_CREATED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_USER_REMOVED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_PROJECT_CREATED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_DIST_PROJECT_BUILD {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_WORKSPACE_DESTROYED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_PROJECT_DESTROYED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_USER_INVITE {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    EVENT_COUNT_JREBEL_USAGE {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    },

    ACTIVE_WORKSPACES {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    ACTIVE_PROJECTS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetListStringValueData.class;
        }
    },

    ACTIVE_USERS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    DETAILS_USER_ADDED_TO_WS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return MapStringLongValueData.class;
        }
    },

    DETAILS_PROJECT_CREATED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return ListListStringValueData.class;
        }
    },

    DETAILS_USER_CREATED {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    DETAILS_APPLICATION_CREATED_PAAS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            // TODO replace by SetValueData<T>
            return MapStringLongValueData.class;
        }
    },

    DETAILS_USER_SSO_LOGGED_IN {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return MapStringLongValueData.class;
        }
    },

    DETAILS_JREBEL_USAGE {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return MapStringLongValueData.class;
        }
    },

    USERS_WITHOUT_PROJECTS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    USERS_WITHOUT_BUILDS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    USERS_WITHOUT_DEPLOYS {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    USERS_WITHOUT_INVITES {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return SetStringValueData.class;
        }
    },

    PRODUCT_USAGE_TIME {
        @Override
        public Class< ? extends ValueData> getValueDataClass() {
            return LongValueData.class;
        }
    };

    /** @return what date type is represented in result */
    public abstract Class< ? extends ValueData> getValueDataClass();

    /** @return list of mandatory parameters required to be passed to the script */
    public Set<MetricParameter> getParams() {
        return new LinkedHashSet<MetricParameter>(
                                                  Arrays.asList(new MetricParameter[]{MetricParameter.FROM_DATE,
                                                          MetricParameter.TO_DATE}));
    }
}
