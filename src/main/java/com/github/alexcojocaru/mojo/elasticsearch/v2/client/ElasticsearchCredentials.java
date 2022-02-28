package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

/**
 * Credentials to use on REST API requests to Elasticsearch.
 * 
 * @author Alex Cojocaru
 */
public class ElasticsearchCredentials
{
    
    private final String username = "elastic";
    private final String password;

    private ElasticsearchCredentials(String password)
    {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder
    {
        private String password;

        public Builder withPassword(String password)
        {
            this.password = password;
            return this;
        }
        
        public ElasticsearchCredentials build()
        {
            return new ElasticsearchCredentials(password);
        }
    }
}
