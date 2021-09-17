/*
 * The MIT License
 *
 * Copyright (c) 2017, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins;

import hudson.model.User;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.JenkinsSessionRule;
import org.jvnet.hudson.test.RestartableJenkinsRule;

public class GithubSecretStorageTest {

    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    @Test
    public void correctBehavior() throws Throwable {
        sessions.then(j -> {
                User.getById("alice", true);
                User.getById("bob", true);

                String secret = "$3cR3t";

                Assert.assertFalse(GithubSecretStorage.contains(retrieveUser()));
                Assert.assertNull(GithubSecretStorage.retrieve(retrieveUser()));

                Assert.assertFalse(GithubSecretStorage.contains(retrieveOtherUser()));

                GithubSecretStorage.put(retrieveUser(), secret);

                Assert.assertTrue(GithubSecretStorage.contains(retrieveUser()));
                Assert.assertFalse(GithubSecretStorage.contains(retrieveOtherUser()));

                Assert.assertEquals(secret, GithubSecretStorage.retrieve(retrieveUser()));
        });
    }

    private User retrieveUser() {
        return User.getById("alice", false);
    }

    private User retrieveOtherUser() {
        return User.getById("bob", false);
    }

    @Test
    public void correctBehaviorEvenAfterRestart() throws Throwable {
        final String secret = "$3cR3t";

        sessions.then(j -> {
                User.getById("alice", true).save();
                User.getById("bob", true).save();

                Assert.assertFalse(GithubSecretStorage.contains(retrieveUser()));
                Assert.assertNull(GithubSecretStorage.retrieve(retrieveUser()));

                Assert.assertFalse(GithubSecretStorage.contains(retrieveOtherUser()));

                GithubSecretStorage.put(retrieveUser(), secret);

                Assert.assertTrue(GithubSecretStorage.contains(retrieveUser()));
                Assert.assertFalse(GithubSecretStorage.contains(retrieveOtherUser()));

                Assert.assertEquals(secret, GithubSecretStorage.retrieve(retrieveUser()));
        });
        sessions.then(j -> {
                Assert.assertTrue(GithubSecretStorage.contains(retrieveUser()));
                Assert.assertFalse(GithubSecretStorage.contains(retrieveOtherUser()));

                Assert.assertEquals(secret, GithubSecretStorage.retrieve(retrieveUser()));
        });
    }
}
