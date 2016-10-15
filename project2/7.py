import gym
import random

# WEST = 0
# SOUTH = 1
# EAST = 2
# NORTH = 3
direction = ['W', 'S', 'E', 'N']

max_episodes = 100000
epsilon = 0.1
epsilon_decay = 0.0001  # 0.00001 for best results
discount = 0.99
learning_rate = 0.1
learning_decay = 0.00001


def run_algorithm(env, q_dict):
    observation = env.reset()
    done = False
    c = 1
    total_rewards = 0
    global epsilon, learning_rate

    while not done:
        c += 1
        action = get_epsilon_greedy_action(q_dict[observation], env.action_space.n)
        prev_observation = observation
        observation, reward, done, info = env.step(action)
        set_q_value_q(prev_observation, observation, action, reward, q_dict)
        # set_q_value_sarsa(prev_observation, observation, action, reward, q_dict, action)
        epsilon *= 1-epsilon_decay
        # learning_rate *= 1-learning_decay
        total_rewards += reward
    return total_rewards


def initiate_q_dict(total_observations, total_actions):
    q_dict = {}
    for i in range(total_observations):
        q_dict[i] = [0]*total_actions
    return q_dict


def get_epsilon_greedy_action(q_values, total_actions):
    random_nr = random.random()
    # print("1-epsilon:", 1-eps)
    # print("Random nr:", random_nr)
    if 1-epsilon > random_nr and max(q_values) != 0:
        best_action = q_values.index(max(q_values))
        # print("Performing best action:", arrows[best_action])
        return best_action
    random_action = random.randint(0, total_actions-1)
    # print(random_action)
    # print("Performing random action:", arrows[random_action])
    return random_action


def set_q_value_sarsa(prev_observation, observation, direction, reward, q_dict, action):
    q_dict[prev_observation][direction] += \
        learning_rate*(reward + discount*q_dict[observation][action] - q_dict[prev_observation][direction])
    # Q(s_t, a_t) += a[r_t+1 + Y * (max(a) Q(s_t+1, a)) - Q(s_t, a_t)]


def set_q_value_q(prev_observation, observation, direction, reward, q_dict):
    q_dict[prev_observation][direction] += learning_rate*(reward + discount*(max(q_dict[observation])) - q_dict[prev_observation][direction])
    # Q(s_t, a_t) += a[r_t+1 + Y * (max(a) Q(s_t+1, a)) - Q(s_t, a_t)]


def print_env(env):
    env.render()
    print()


def main():
    total_rewards = 0
    env = gym.make('Taxi-v1')
    # env = gym.make('FrozenLake-v0')
    q_dict = initiate_q_dict(env.observation_space.n, env.action_space.n)
    episode = 0
    while episode < max_episodes:
        total_rewards += run_algorithm(env, q_dict)
        episode += 1
        if episode % 100 == 0 or episode == 1:
            print(total_rewards/episode)
        # if episode % 100 == 0:
        #     print(total_rewards/100)
        #     total_rewards = 0

    # env.render()

# 9.879616   1 mill

main()
